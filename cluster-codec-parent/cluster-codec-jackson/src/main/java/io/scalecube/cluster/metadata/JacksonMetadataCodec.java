package io.scalecube.cluster.metadata;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import io.scalecube.cluster.membership.MembershipEvent.Type;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import reactor.core.Exceptions;

public abstract class JacksonMetadataCodec implements MetadataEncoder, MetadataDecoder {

  public static final ObjectMapper OBJECT_MAPPER = initMapper();

  private static ObjectMapper initMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
    mapper.enableDefaultTyping(DefaultTyping.JAVA_LANG_OBJECT, JsonTypeInfo.As.PROPERTY);
    return mapper;
  }

  @Override
  public Object decode(ByteBuffer buffer, Type type) {
    try {
      if (buffer.remaining() == 0) {
        return null;
      }
      return OBJECT_MAPPER.readValue(new ByteBufferBackedInputStream(buffer), type);
    } catch (Exception e) {
      throw Exceptions.propagate(e);
    }
  }

  @Override
  public ByteBuffer encode(Object metadata) {
    try {
      return ByteBuffer.wrap(OBJECT_MAPPER.writeValueAsBytes(metadata));
    } catch (Exception e) {
      throw Exceptions.propagate(e);
    }
  }
}
