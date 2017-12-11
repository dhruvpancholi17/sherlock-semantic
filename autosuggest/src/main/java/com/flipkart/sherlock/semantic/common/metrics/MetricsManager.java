package com.flipkart.sherlock.semantic.common.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.flipkart.sherlock.semantic.common.util.JmxMetricRegistry;
import com.netflix.hystrix.contrib.codahalemetricspublisher.HystrixCodaHaleMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Created by anurag.laddha on 02/08/17.
 */


/**
 * Helps with tracking metrics
 * <p>
 * Usage
 * 1. Call init() with actions to be traced for components within service/namespace
 * 2. Call suitable log method
 */
@Slf4j
public class MetricsManager {


    /**
     * Type of action to be tracked
     * Depending on type of action, timer or meter is chosen
     */
    public enum ActionType {
        LATENCY("latency"),
        SUCCESS("success"),
        ERROR("error"),
        NULL("null"),
        RPS("rps");

        private ActionType(String s) {
            metricType = s;
        }

        private final String metricType;

        public String getMetricType() {
            return metricType;
        }
    }

    /**
     * Serves as namespace/isolation
     * To make this manager generic, use string type instead of enum for namespace
     */
    public enum Service {
        SemanticCommon("common"),
        Autosuggest("autosuggest");

        private Service(String s) {
            serviceName = s;
        }

        private final String serviceName;

        public String getServiceName() {
            return serviceName;
        }
    }


    /**
     * Details of each tracing action to be taken
     */
    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class TracedItem {
        private ActionType actionType;
        private Service service;
        private String component;
    }

    /**
     * Init needs to be called once before using the service
     */
    public static void init(Set<TracedItem> tracedItems) {
        if (!initialised && tracedItems != null) {

            // Exposes hystrix metrics using given metric registry.
            HystrixPlugins.getInstance().registerMetricsPublisher(new HystrixCodaHaleMetricsPublisher(
                    JmxMetricRegistry.INSTANCE.getInstance()));

            for (TracedItem currItem : tracedItems) {
                Service service = currItem.getService();
                String componentName = currItem.getComponent();

                String metricName = MetricRegistry.name(METRIC_PREFIX, service.name(), currItem.getActionType().getMetricType(),
                        componentName);

                switch (currItem.getActionType()) {
                    case ERROR:
                        serviceComponentToErrorMeter.putIfAbsent(service, new HashMap<>());
                        serviceComponentToErrorMeter.get(service).put(componentName, getMetricRegistry().meter(metricName));
                        break;
                    case NULL:
                        serviceComponentToNullMeter.putIfAbsent(service, new HashMap<>());
                        serviceComponentToNullMeter.get(service).put(componentName, getMetricRegistry().meter(metricName));
                        break;
                    case RPS:
                        serviceComponentToRPSMeter.putIfAbsent(service, new HashMap<>());
                        serviceComponentToRPSMeter.get(service).put(componentName, getMetricRegistry().meter(metricName));
                        break;
                    case SUCCESS:
                        serviceComponentToSuccessMeter.putIfAbsent(service, new HashMap<>());
                        serviceComponentToSuccessMeter.get(service).put(componentName, getMetricRegistry().meter(metricName));
                        break;
                    case LATENCY:
                        serviceComponentToLatencyTimer.putIfAbsent(service, new HashMap<>());
                        serviceComponentToLatencyTimer.get(service).put(componentName, getMetricRegistry().timer(metricName));
                        break;
                    default:
                        throw new RuntimeException("Unknown action type");
                }
            }
            initialised = true;
        }
    }

    private static final String METRIC_PREFIX = "com.flipkart.sherlock.semantic";
    private static boolean initialised = false;

    //To avoid creating object to locate required metric.
    private static Map<Service, Map<String, Meter>> serviceComponentToRPSMeter = new HashMap<>();
    private static Map<Service, Map<String, Meter>> serviceComponentToSuccessMeter = new HashMap<>();
    private static Map<Service, Map<String, Meter>> serviceComponentToErrorMeter = new HashMap<>();
    private static Map<Service, Map<String, Meter>> serviceComponentToNullMeter = new HashMap<>();
    private static Map<Service, Map<String, Timer>> serviceComponentToLatencyTimer = new HashMap<>();

    /**
     * Track RPS/throughput for given service and component
     */
    public static void logRequests(Service service, String component) {
        if (StringUtils.isNotBlank(component)) {
            validateInitialised();
            validateMeterAvailable(serviceComponentToRPSMeter, service, component);
            serviceComponentToRPSMeter.get(service).get(component).mark();
        }
    }

    /**
     * Tracks success rate for given service and component
     */
    public static void logSuccess(Service service, String component) {
        if (StringUtils.isNotBlank(component)) {
            validateInitialised();
            validateMeterAvailable(serviceComponentToSuccessMeter, service, component);
            serviceComponentToSuccessMeter.get(service).get(component).mark();
        }
    }

    /**
     * Tracks error rate for given service and component
     */
    public static void logError(Service service, String component) {
        if (StringUtils.isNotBlank(component)) {
            validateInitialised();
            validateMeterAvailable(serviceComponentToErrorMeter, service, component);
            serviceComponentToErrorMeter.get(service).get(component).mark();
        }
    }

    /**
     * Tracks error rate, and also prints errors
     */
    public static void logError(Service service, String component, Exception e) {
        log.error("{}", e);
        logError(service, component);
    }


    /**
     * Tracks null response rate for given service and component
     */
    public static void logNullResponse(Service service, String component) {
        if (StringUtils.isNotBlank(component)) {
            validateInitialised();
            validateMeterAvailable(serviceComponentToNullMeter, service, component);
            serviceComponentToNullMeter.get(service).get(component).mark();
        }
    }

    /**
     * Tracks time taken to execute given runnable
     */
    public static void logTime(Service service, String component, Runnable runnable) {

        if (StringUtils.isNotBlank(component) && runnable != null) {
            validateTimerAvailable(serviceComponentToLatencyTimer, service, component);
            final Timer.Context context = serviceComponentToLatencyTimer.get(service).get(component).time();
            try {
                runnable.run();
            } catch (Exception e) {
                logError(service, component, e);
            } finally {
                context.stop();
            }
        }
    }

    /**
     * Tracks time taken to execute given supplier.
     * Use this to return value
     */
    public static <R> R logTime(Service service, String component, Supplier<R> supplier) {
        if (StringUtils.isNotBlank(component) && supplier != null) {
            validateTimerAvailable(serviceComponentToLatencyTimer, service, component);
            final Timer.Context context = serviceComponentToLatencyTimer.get(service).get(component).time();
            try {
                return supplier.get();
            } finally {
                context.stop();
            }
        }
        return null;
    }


    private static void validateInitialised() {
        if (!initialised) {
            throw new RuntimeException("Metrics manager used without initialisation");
        }
    }

    private static void validateMeterAvailable(Map<Service, Map<String, Meter>> srvcComponentToMeterMap, Service service,
                                               String component) {
        if (srvcComponentToMeterMap == null || !srvcComponentToMeterMap.containsKey(service) ||
                !srvcComponentToMeterMap.get(service).containsKey(component)) {
            throw new RuntimeException(String.format("Meter not available for service: %s, component: %s", service.name(), component));
        }
    }

    private static void validateTimerAvailable(Map<Service, Map<String, Timer>> srvcComponentToTimerMap, Service service,
                                               String component) {
        if (srvcComponentToTimerMap == null || !srvcComponentToTimerMap.containsKey(service) ||
                !srvcComponentToTimerMap.get(service).containsKey(component)) {
            throw new RuntimeException(String.format("Timer not available for service: %s, component: %s", service.name(), component));
        }
    }

    private static MetricRegistry getMetricRegistry() {
        return JmxMetricRegistry.INSTANCE.getInstance();
    }

}