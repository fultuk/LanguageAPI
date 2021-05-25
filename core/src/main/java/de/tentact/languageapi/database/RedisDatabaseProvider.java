/*
 * MIT License
 *
 * Copyright (c) 2021 0utplay (Aldin Sijamhodzic)
 * Copyright (c) 2021 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.tentact.languageapi.database;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

import java.io.*;
import java.nio.ByteBuffer;

public class RedisDatabaseProvider {

  private final String hostname;
  private final String database;
  private final String username;
  private final String password;
  private final int port;
  private final StatefulRedisConnection<Object, Object> connection;

  protected RedisDatabaseProvider(String hostname, String database, String username, String password, int port) {
    this.hostname = hostname;
    this.database = database;
    this.username = username;
    this.password = password;
    this.port = port;

    RedissonClient redissonClient = Redisson.create();
    redissonClient.createBatch().

    RedisClient redisClient = RedisClient.create();
    this.connection = redisClient.connect(new CustomCodec());
  }

  public StatefulRedisConnection<Object, Object> getConnection() {
    return this.connection;
  }

  static class CustomCodec implements RedisCodec<Object, Object> {

    @Override
    public Object decodeKey(ByteBuffer bytes) {
      try {
        byte[] array = new byte[bytes.remaining()];
        bytes.get(array);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(array));
        return objectInputStream.readObject();
      } catch (Exception e) {
        return null;
      }
    }

    @Override
    public Object decodeValue(ByteBuffer bytes) {
      try {
        byte[] array = new byte[bytes.remaining()];
        bytes.get(array);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(array));
        return objectInputStream.readObject();
      } catch (Exception e) {
        return null;
      }
    }

    @Override
    public ByteBuffer encodeKey(Object key) {
      try {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytes);
        objectOutputStream.writeObject(key);
        return ByteBuffer.wrap(bytes.toByteArray());
      } catch (IOException e) {
        return null;
      }
    }

    @Override
    public ByteBuffer encodeValue(Object value) {
      try {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytes);
        objectOutputStream.writeObject(value);
        return ByteBuffer.wrap(bytes.toByteArray());
      } catch (IOException e) {
        return null;
      }
    }
  }
}
