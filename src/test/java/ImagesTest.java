import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.images.Eyes;
import com.applitools.eyes.images.ImageRunner;

import com.applitools.eyes.images.Target;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImagesTest
{
    private static EyesRunner runner = new ImageRunner();

    private String testName;
    Eyes eyes;
    private static BatchInfo batch = new BatchInfo("Images Test");
    boolean establishBaseline = true;
    String usaToday = establishBaseline ? "usatodayBefore" : "usatodayAfter";
    String applitools = establishBaseline ? "applitoolsBefore" : "applitoolsAfter";
    String fb = establishBaseline ? "fbBefore" : "fbAfter";


    @BeforeEach
    public void setup( TestInfo testInfo) {
        testName = testInfo.getDisplayName();
    }

    @Test
    public void exactTest() {
        checkImage(MatchLevel.EXACT);
    }

    @Test
    public void strictTest() {
        checkImage(MatchLevel.STRICT);
    }

    @Test
    public void ignoreColorsTest() {
        checkImage(MatchLevel.IGNORE_COLORS);
    }

    @Test
    public void layoutTest() {
        checkImage(MatchLevel.LAYOUT2);
    }

    private void checkImage(MatchLevel matchLevel) {
        eyes = new Eyes(runner);
        runner.setDontCloseBatches(true);
        Configuration config = eyes.getConfiguration();
        config.setSaveDiffs(establishBaseline);
        config.setBatch(batch);
        eyes.open(matchLevel.getName(), testName, new RectangleSize(1420, 800));

        BufferedImage usaTodayImage = null;
        BufferedImage applitoolsImage = null;
        BufferedImage fbImage = null;

        try {
            usaTodayImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream(usaToday + ".png"));
            applitoolsImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream(applitools + ".png"));
            fbImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream(fb + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Visual validation.
        eyes.check(Target.image(usaTodayImage).matchLevel(matchLevel));
        eyes.check(Target.image(applitoolsImage).matchLevel(matchLevel));
        eyes.check(Target.image(fbImage).matchLevel(matchLevel));
        eyes.closeAsync();
        runner.getAllTestResults();
    }

    @AfterAll
    public static void teardown() {
        //BatchClose batchClose = new BatchClose();
        //batchClose.setBatchId(Arrays.asList(batchId)).close();
    }
}