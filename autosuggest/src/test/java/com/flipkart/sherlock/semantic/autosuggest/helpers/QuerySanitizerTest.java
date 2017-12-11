package com.flipkart.sherlock.semantic.autosuggest.helpers;

import com.flipkart.sherlock.semantic.autosuggest.helpers.QuerySanitizer.QueryPrefix;
import org.junit.Assert;
import org.junit.Test;

import static com.flipkart.sherlock.semantic.autosuggest.helpers.QuerySanitizer.getQueryPrefix;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class QuerySanitizerTest {

    @Test
    public void testGetQueryPrefix() {
        Assert.assertEquals(new QueryPrefix("gear for cyc", "gear for", "cyc"), getQueryPrefix("gear for cyc"));
        Assert.assertEquals(new QueryPrefix("galaxy bn j7", "galaxy bn", "j7"), getQueryPrefix("galaxy bn j7"));
        Assert.assertEquals(new QueryPrefix("ipro battry sa", "ipro battry", "sa"), getQueryPrefix("ipro battry sa"));
        Assert.assertEquals(new QueryPrefix("jaa drill ada", "jaa drill", "ada"), getQueryPrefix("jaa drill ada"));
        Assert.assertEquals(new QueryPrefix("dailyobj n", "dailyobj", "n"), getQueryPrefix("dailyobj n"));
        Assert.assertEquals(new QueryPrefix("sneskers for wo", "sneskers for", "wo"), getQueryPrefix("sneskers for wo"));
        Assert.assertEquals(new QueryPrefix("part wear shot salw", "part wear shot", "salw"), getQueryPrefix("part wear shot salw"));
        Assert.assertEquals(new QueryPrefix("patinjali endurams", "patinjali", "endurams"), getQueryPrefix("patinjali endurams"));
        Assert.assertEquals(new QueryPrefix("purple a line k", "purple a line", "k"), getQueryPrefix("purple a line k"));
        Assert.assertEquals(new QueryPrefix("manchester united iphone cas", "manchester united iphone", "cas"), getQueryPrefix("manchester united iphone cas"));
        Assert.assertEquals(new QueryPrefix("jspy", "", "jspy"), getQueryPrefix("jspy"));
        Assert.assertEquals(new QueryPrefix("ruggers mens casual lllshirts", "ruggers mens casual", "lllshirts"), getQueryPrefix("ruggers mens casual lllshirts"));
        Assert.assertEquals(new QueryPrefix("addidas crezy expl", "addidas crezy", "expl"), getQueryPrefix("addidas crezy expl"));
        Assert.assertEquals(new QueryPrefix("toys tent hoy", "toys tent", "hoy"), getQueryPrefix("toys tent hoy"));
        Assert.assertEquals(new QueryPrefix("backcover micromax q38 uder rs", "backcover micromax q38 uder", "rs"), getQueryPrefix("backcover micromax q38 uder rs"));
        Assert.assertEquals(new QueryPrefix("samsung galaxy tab a back cover for gi", "samsung galaxy tab a back cover for", "gi"), getQueryPrefix("samsung galaxy tab a back cover for gi"));
        Assert.assertEquals(new QueryPrefix("tecozo back cover for apple iphone 7", "tecozo back cover for apple iphone", "7"), getQueryPrefix("tecozo back cover for apple iphone 7"));
        Assert.assertEquals(new QueryPrefix("cute paper clips", "cute paper", "clips"), getQueryPrefix("cute paper clips"));
        Assert.assertEquals(new QueryPrefix("indian lakhani shoes in sports for", "indian lakhani shoes in sports", "for"), getQueryPrefix("indian lakhani shoes in sports for"));
        Assert.assertEquals(new QueryPrefix("canon laserprinter", "canon", "laserprinter"), getQueryPrefix("canon laserprinter"));
        Assert.assertEquals(new QueryPrefix("bounce back leather case cover for vivo y51", "bounce back leather case cover for vivo", "y51"), getQueryPrefix("bounce back leather case cover for vivo y51"));
        Assert.assertEquals(new QueryPrefix("sim activesan morpho", "sim activesan", "morpho"), getQueryPrefix("sim activesan morpho"));
        Assert.assertEquals(new QueryPrefix("vodeocon krypron 3", "vodeocon krypron", "3"), getQueryPrefix("vodeocon krypron 3"));
        Assert.assertEquals(new QueryPrefix("cannig2000 original ink", "cannig2000 original", "ink"), getQueryPrefix("cannig2000 original ink"));
        Assert.assertEquals(new QueryPrefix("pants and shirts f", "pants and shirts", "f"), getQueryPrefix("pants and shirts f"));
        Assert.assertEquals(new QueryPrefix("parisvelly pe", "parisvelly", "pe"), getQueryPrefix("parisvelly pe"));
        Assert.assertEquals(new QueryPrefix("money loking wa", "money loking", "wa"), getQueryPrefix("money loking wa"));
        Assert.assertEquals(new QueryPrefix("deepika padukone style", "deepika padukone", "style"), getQueryPrefix("deepika padukone style"));
        Assert.assertEquals(new QueryPrefix("zedfo case flip cover for samsung galaxy on", "zedfo case flip cover for samsung galaxy", "on"), getQueryPrefix("zedfo case flip cover for samsung galaxy on"));
        Assert.assertEquals(new QueryPrefix("wedding chuda red c", "wedding chuda red", "c"), getQueryPrefix("wedding chuda red c"));
        Assert.assertEquals(new QueryPrefix("keyboard for micromax q33", "keyboard for micromax", "q33"), getQueryPrefix("keyboard for micromax q33"));
        Assert.assertEquals(new QueryPrefix("baby chair for", "baby chair", "for"), getQueryPrefix("baby chair for"));
        Assert.assertEquals(new QueryPrefix("temperglass protector for iball comp", "temperglass protector for iball", "comp"), getQueryPrefix("temperglass protector for iball comp"));
        Assert.assertEquals(new QueryPrefix("sarwal kameg ny", "sarwal kameg", "ny"), getQueryPrefix("sarwal kameg ny"));
        Assert.assertEquals(new QueryPrefix("rs aggarwalmath", "rs", "aggarwalmath"), getQueryPrefix("rs aggarwalmath"));
        Assert.assertEquals(new QueryPrefix("two way a", "two way", "a"), getQueryPrefix("two way a"));
        Assert.assertEquals(new QueryPrefix("dolls for m", "dolls for", "m"), getQueryPrefix("dolls for m"));
        Assert.assertEquals(new QueryPrefix("electric aata maker%26 many mo", "electric aata maker%26 many", "mo"), getQueryPrefix("electric aata maker%26 many mo"));
        Assert.assertEquals(new QueryPrefix("aerial sirf", "aerial", "sirf"), getQueryPrefix("aerial sirf"));
        Assert.assertEquals(new QueryPrefix("today offerin p", "today offerin", "p"), getQueryPrefix("today offerin p"));
        Assert.assertEquals(new QueryPrefix("dress child 6 mon", "dress child 6", "mon"), getQueryPrefix("dress child 6 mon"));
        Assert.assertEquals(new QueryPrefix("steel kookin", "steel", "kookin"), getQueryPrefix("steel kookin"));
        Assert.assertEquals(new QueryPrefix("kinde rechrga", "kinde", "rechrga"), getQueryPrefix("kinde rechrga"));
        Assert.assertEquals(new QueryPrefix("lenveo k8 note 64 g", "lenveo k8 note 64", "g"), getQueryPrefix("lenveo k8 note 64 g"));
        Assert.assertEquals(new QueryPrefix("mar q air con", "mar q air", "con"), getQueryPrefix("mar q air con"));
        Assert.assertEquals(new QueryPrefix("i phone7p", "i", "phone7p"), getQueryPrefix("i phone7p"));
        Assert.assertEquals(new QueryPrefix("smart watches for men u", "smart watches for men", "u"), getQueryPrefix("smart watches for men u"));
        Assert.assertEquals(new QueryPrefix("ankel lenth legins", "ankel lenth", "legins"), getQueryPrefix("ankel lenth legins"));
        Assert.assertEquals(new QueryPrefix("ether olive jacke", "ether olive", "jacke"), getQueryPrefix("ether olive jacke"));
        Assert.assertEquals(new QueryPrefix("proteen for", "proteen", "for"), getQueryPrefix("proteen for"));
        Assert.assertEquals(new QueryPrefix("assc", "", "assc"), getQueryPrefix("assc"));
        Assert.assertEquals(new QueryPrefix("kidstopingirl", "", "kidstopingirl"), getQueryPrefix("kidstopingirl"));
        Assert.assertEquals(new QueryPrefix("iot fundamentals", "iot", "fundamentals"), getQueryPrefix("iot fundamentals"));
        Assert.assertEquals(new QueryPrefix("home theatre 5.1 under 3000", "home theatre 5.1 under", "3000"), getQueryPrefix("home theatre 5.1 under 3000"));
        Assert.assertEquals(new QueryPrefix("backcover micromax q38 uder rs 1", "backcover micromax q38 uder rs", "1"), getQueryPrefix("backcover micromax q38 uder rs 1"));
        Assert.assertEquals(new QueryPrefix("multiplayer cutter wi", "multiplayer cutter", "wi"), getQueryPrefix("multiplayer cutter wi"));
        Assert.assertEquals(new QueryPrefix("20 lips", "20", "lips"), getQueryPrefix("20 lips"));
        Assert.assertEquals(new QueryPrefix("adidas sweat shirt", "adidas sweat", "shirt"), getQueryPrefix("adidas sweat shirt"));
        Assert.assertEquals(new QueryPrefix("home theater tickets series", "home theater tickets", "series"), getQueryPrefix("home theater tickets series"));
        Assert.assertEquals(new QueryPrefix("goitha", "", "goitha"), getQueryPrefix("goitha"));
        Assert.assertEquals(new QueryPrefix("royal enfield bhadbhada awazsilencer", "royal enfield bhadbhada", "awazsilencer"), getQueryPrefix("royal enfield bhadbhada awazsilencer"));
        Assert.assertEquals(new QueryPrefix("sliv5", "", "sliv5"), getQueryPrefix("sliv5"));
        Assert.assertEquals(new QueryPrefix("7 years boys elect", "7 years boys", "elect"), getQueryPrefix("7 years boys elect"));
        Assert.assertEquals(new QueryPrefix("office wall sticker of", "office wall sticker", "of"), getQueryPrefix("office wall sticker of"));
        Assert.assertEquals(new QueryPrefix("foodfor 4 years girl", "foodfor 4 years", "girl"), getQueryPrefix("foodfor 4 years girl"));
        Assert.assertEquals(new QueryPrefix("branded running leather s", "branded running leather", "s"), getQueryPrefix("branded running leather s"));
        Assert.assertEquals(new QueryPrefix("orpad", "", "orpad"), getQueryPrefix("orpad"));
        Assert.assertEquals(new QueryPrefix("iphone 7 plus cover for bo", "iphone 7 plus cover for", "bo"), getQueryPrefix("iphone 7 plus cover for bo"));
        Assert.assertEquals(new QueryPrefix("home theatar sonr", "home theatar", "sonr"), getQueryPrefix("home theatar sonr"));
        Assert.assertEquals(new QueryPrefix("karpet bi", "karpet", "bi"), getQueryPrefix("karpet bi"));
        Assert.assertEquals(new QueryPrefix("mercury pc cabin", "mercury pc", "cabin"), getQueryPrefix("mercury pc cabin"));
        Assert.assertEquals(new QueryPrefix("vandvshop.c", "", "vandvshop.c"), getQueryPrefix("vandvshop.c"));
        Assert.assertEquals(new QueryPrefix("best wrist watch mrn", "best wrist watch", "mrn"), getQueryPrefix("best wrist watch mrn"));
        Assert.assertEquals(new QueryPrefix("redmi 4a in black", "redmi 4a in", "black"), getQueryPrefix("redmi 4a in black"));
        Assert.assertEquals(new QueryPrefix("lg oled signatu tv", "lg oled signatu", "tv"), getQueryPrefix("lg oled signatu tv"));
        Assert.assertEquals(new QueryPrefix("boosah womens to", "boosah womens", "to"), getQueryPrefix("boosah womens to"));
        Assert.assertEquals(new QueryPrefix("clymbe sho", "clymbe", "sho"), getQueryPrefix("clymbe sho"));
        Assert.assertEquals(new QueryPrefix("dports fi", "dports", "fi"), getQueryPrefix("dports fi"));
        Assert.assertEquals(new QueryPrefix("samsung frigde black 19", "samsung frigde black", "19"), getQueryPrefix("samsung frigde black 19"));
        Assert.assertEquals(new QueryPrefix("gatreed corn c", "gatreed corn", "c"), getQueryPrefix("gatreed corn c"));
        Assert.assertEquals(new QueryPrefix("zenfone 3 pa", "zenfone 3", "pa"), getQueryPrefix("zenfone 3 pa"));
        Assert.assertEquals(new QueryPrefix("belo 500 pressure cooker 3 liter", "belo 500 pressure cooker 3", "liter"), getQueryPrefix("belo 500 pressure cooker 3 liter"));
        Assert.assertEquals(new QueryPrefix("panasonic led s43 inch led tv", "panasonic led s43 inch led", "tv"), getQueryPrefix("panasonic led s43 inch led tv"));
        Assert.assertEquals(new QueryPrefix("bt686rbf", "", "bt686rbf"), getQueryPrefix("bt686rbf"));
        Assert.assertEquals(new QueryPrefix("lacha", "", "lacha"), getQueryPrefix("lacha"));
        Assert.assertEquals(new QueryPrefix("karbon mobile under", "karbon mobile", "under"), getQueryPrefix("karbon mobile under"));
        Assert.assertEquals(new QueryPrefix("titan strap", "titan", "strap"), getQueryPrefix("titan strap"));
        Assert.assertEquals(new QueryPrefix("yellow colur patiyala s", "yellow colur patiyala", "s"), getQueryPrefix("yellow colur patiyala s"));
        Assert.assertEquals(new QueryPrefix("bleuthooth f", "bleuthooth", "f"), getQueryPrefix("bleuthooth f"));
        Assert.assertEquals(new QueryPrefix("black stonering for mens", "black stonering for", "mens"), getQueryPrefix("black stonering for mens"));
        Assert.assertEquals(new QueryPrefix("champions trophy adida football official", "champions trophy adida football", "official"), getQueryPrefix("champions trophy adida football official"));
        Assert.assertEquals(new QueryPrefix("cover for lyf wind", "cover for lyf", "wind"), getQueryPrefix("cover for lyf wind"));
        Assert.assertEquals(new QueryPrefix("bangoli bang", "bangoli", "bang"), getQueryPrefix("bangoli bang"));
        Assert.assertEquals(new QueryPrefix("panasonic power b", "panasonic power", "b"), getQueryPrefix("panasonic power b"));
        Assert.assertEquals(new QueryPrefix("jockey printed mens underw", "jockey printed mens", "underw"), getQueryPrefix("jockey printed mens underw"));
        Assert.assertEquals(new QueryPrefix("jact zara men", "jact zara", "men"), getQueryPrefix("jact zara men"));
        Assert.assertEquals(new QueryPrefix("42im", "", "42im"), getQueryPrefix("42im"));
    }
}
