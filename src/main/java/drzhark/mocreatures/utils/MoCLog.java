package drzhark.mocreatures.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoCLog {
  public static final Logger logger = LogManager.getLogger("MoCreatures");
  
  public static void initLog() {
    logger.info("Starting MoCreatures");
  }
}
