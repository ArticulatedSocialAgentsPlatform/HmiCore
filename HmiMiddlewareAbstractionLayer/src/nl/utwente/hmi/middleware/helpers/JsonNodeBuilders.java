/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package nl.utwente.hmi.middleware.helpers;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Convenience {@link JsonNode} builder.
 */
@NoArgsConstructor(access = PRIVATE)
public final class JsonNodeBuilders {

  /**
   * Factory methods for an {@link ObjectNode} builder.
   */

  public static ObjectNodeBuilder object() {
    return object(JsonNodeFactory.instance);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, boolean v1) {
    return object().with(k1, v1);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, int v1) {
    return object().with(k1, v1);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, float v1) {
    return object().with(k1, (double)v1);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, double v1) {
    return object().with(k1, v1);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, String v1) {
    return object().with(k1, v1);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, String v1, @NonNull String k2, String v2) {
    return object(k1, v1).with(k2, v2);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, String v1, @NonNull String k2, String v2,
      @NonNull String k3, String v3) {
    return object(k1, v1, k2, v2).with(k3, v3);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, JsonNodeBuilder<?> builder) {
    return object().with(k1, builder);
  }

  public static ObjectNodeBuilder object(JsonNodeFactory factory) {
    return new ObjectNodeBuilder(factory);
  }

  /**
   * Factory methods for an {@link ArrayNode} builder.
   */

  public static ArrayNodeBuilder array() {
    return array(JsonNodeFactory.instance);
  }

  public static ArrayNodeBuilder array(@NonNull boolean... values) {
    return array().with(values);
  }

  public static ArrayNodeBuilder array(@NonNull int... values) {
    return array().with(values);
  }
  
  public static ArrayNodeBuilder array(@NonNull float... values) {
	return array().with(values);
  }

  public static ArrayNodeBuilder array(@NonNull String... values) {
    return array().with(values);
  }

  public static ArrayNodeBuilder array(@NonNull JsonNodeBuilder<?>... builders) {
    return array().with(builders);
  }

  public static ArrayNodeBuilder array(JsonNodeFactory factory) {
    return new ArrayNodeBuilder(factory);
  }

  public interface JsonNodeBuilder<T extends JsonNode> {

    /**
     * Construct and return the {@link JsonNode} instance.
     */
    T end();

  }

  @RequiredArgsConstructor
  private static abstract class AbstractNodeBuilder<T extends JsonNode> implements JsonNodeBuilder<T> {

    /**
     * The source of values.
     */
    @NonNull
    protected final JsonNodeFactory factory;

    /**
     * The value under construction.
     */
    @NonNull
    protected final T node;

    /**
     * Returns a valid JSON string, so long as {@code POJONode}s not used.
     */
    @Override
    public String toString() {
      return node.toString();
    }

  }

  public final static class ObjectNodeBuilder extends AbstractNodeBuilder<ObjectNode> {

    private ObjectNodeBuilder(JsonNodeFactory factory) {
      super(factory, factory.objectNode());
    }

    public ObjectNodeBuilder withNull(@NonNull String field) {
      return with(field, factory.nullNode());
    }

    public ObjectNodeBuilder with(@NonNull String field, int value) {
      return with(field, factory.numberNode(value));
    }

    public ObjectNodeBuilder with(@NonNull String field, float value) {
      return with(field, (double)value);
    }

    public ObjectNodeBuilder with(@NonNull String field, double value) {
      return with(field, factory.numberNode(value));
    }

    public ObjectNodeBuilder with(@NonNull String field, boolean value) {
      return with(field, factory.booleanNode(value));
    }

    public ObjectNodeBuilder with(@NonNull String field, String value) {
      return with(field, factory.textNode(value));
    }

    public ObjectNodeBuilder with(@NonNull String field, JsonNode value) {
      node.set(field, value);
      return this;
    }

    public ObjectNodeBuilder with(@NonNull String field, @NonNull JsonNodeBuilder<?> builder) {
      return with(field, builder.end());
    }

    public ObjectNodeBuilder withPOJO(@NonNull String field, @NonNull Object pojo) {
      return with(field, factory.pojoNode(pojo));
    }

    @Override
    public ObjectNode end() {
      return node;
    }

  }

  public final static class ArrayNodeBuilder extends AbstractNodeBuilder<ArrayNode> {

    private ArrayNodeBuilder(JsonNodeFactory factory) {
      super(factory, factory.arrayNode());
    }

    public ArrayNodeBuilder with(boolean value) {
      node.add(value);
      return this;
    }

    public ArrayNodeBuilder with(@NonNull boolean... values) {
      for (val value : values)
        with(value);
      return this;
    }

    public ArrayNodeBuilder with(int value) {
      node.add(value);
      return this;
    }

    public ArrayNodeBuilder with(@NonNull int... values) {
      for (val value : values)
        with(value);
      return this;
    }
    
    public ArrayNodeBuilder with(@NonNull float... values) {
       for (val value : values)
	     with(value);
	   return this;
    }

    public ArrayNodeBuilder with(float value) {
      node.add(value);
      return this;
    }

    public ArrayNodeBuilder with(double value) {
      node.add(value);
      return this;
    }

    public ArrayNodeBuilder with(String value) {
      node.add(value);
      return this;
    }

    public ArrayNodeBuilder with(@NonNull String... values) {
      for (val value : values)
        with(value);
      return this;
    }

    public ArrayNodeBuilder with(@NonNull Iterable<String> values) {
      for (val value : values)
        with(value);
      return this;
    }

    public ArrayNodeBuilder with(JsonNode value) {
      node.add(value);
      return this;
    }

    public ArrayNodeBuilder with(@NonNull JsonNode... values) {
      for (val value : values)
        with(value);
      return this;
    }

    public ArrayNodeBuilder with(JsonNodeBuilder<?> value) {
      return with(value.end());
    }

    public ArrayNodeBuilder with(@NonNull JsonNodeBuilder<?>... builders) {
      for (val builder : builders)
        with(builder);
      return this;
    }

    @Override
    public ArrayNode end() {
      return node;
    }

  }

}
