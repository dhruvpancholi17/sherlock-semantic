CREATE TABLE `plural_singular` (
  `plural` varchar(250) NOT NULL,
  `singular` varchar(250) NOT NULL,
  `enabled` tinyint(1) DEFAULT '1',
  `updated_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `plural_uniq` (`plural`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `brand_info` (
  `brand` varchar(200) NOT NULL,
  `store` varchar(500) NOT NULL,
  `enabled` tinyint(1) DEFAULT '1',
  `updated_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `brand_uniq` (`brand`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;