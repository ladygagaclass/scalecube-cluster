package io.scalecube.cluster.metadata;

import io.scalecube.utils.ServiceLoaderUtil;
import java.nio.ByteBuffer;

@FunctionalInterface
public interface MetadataDecoder {

  MetadataDecoder INSTANCE = ServiceLoaderUtil.findFirst(MetadataDecoder.class).orElse(null);

  @Deprecated
  Object decode(ByteBuffer buffer);

  Object decode(ByteBuffer buffer, Type );

  Object decode(ByteBuffer buffer, Class);

}
