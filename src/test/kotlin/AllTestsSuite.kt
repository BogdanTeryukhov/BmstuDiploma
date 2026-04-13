import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    DebugSplitTest::class,
    EnhancedRopeTest::class,
    EnhancedRopeUtilsTest::class,
    KmpAutomatonTest::class,
    MarkovAlgorithmTest::class,
    RegexAutomatonTest::class
)
class AllTestsSuite