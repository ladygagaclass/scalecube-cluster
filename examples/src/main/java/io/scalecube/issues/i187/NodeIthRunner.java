package io.scalecube.issues.i187;

import io.scalecube.cluster.Cluster;
import io.scalecube.cluster.ClusterConfig;
import io.scalecube.cluster.ClusterImpl;
import io.scalecube.net.Address;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeIthRunner {

  public static final Logger logger = LoggerFactory.getLogger(NodeIthRunner.class);

  public static final int DEFAULT_SEED_PORT = 4545;

  /**
   * Maibn.
   *
   * @param args args
   * @throws Exception exception
   */
  public static void main(String[] args) throws Exception {
    Address address =
        getSeedAddress(args).orElseGet(() -> Address.create("localhost", DEFAULT_SEED_PORT));

    Map<String, String> metadata =
        Collections.singletonMap("node-i-th", Integer.toHexString(new Object().hashCode()));

    ClusterConfig config =
        new ClusterConfig()
            .membership(
                opts ->
                    opts.syncGroup("issue187")
                        .seedMembers(address)
                        .syncInterval(1000)
                        .syncTimeout(1000))
            .transport(opts -> opts.connectTimeout(1000))
            .metadata(metadata)
            .metadataTimeout(1000);

    logger.debug("Starting Node-i-th with config {}", config);
    Cluster cluster = new ClusterImpl(config).startAwait();
    logger.debug("Started Node-i-th: {}, address: {}", cluster, cluster.address());

    Thread.currentThread().join();
  }

  private static Optional<Address> getSeedAddress(String[] args) {
    if (args.length < 1) {
      return Optional.empty();
    }
    String addressArg = args[0];
    if (addressArg.isEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(Address.from(addressArg));
    } catch (Exception ex) {
      logger.error("Error in getSeedAddress: " + ex);
      return Optional.empty();
    }
  }
}
