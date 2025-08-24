/*
 * Copyright 2025 Jared Ottley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.mcp.model;

import java.util.ArrayList;
import java.util.List;

public class OperationResponse<T> {
  private boolean success;
  private List<String> messages = new ArrayList<>(); // error, info, warning, etc.
  private T data; // The returned object(s), could be POJO or collection

  public OperationResponse() {}

  public OperationResponse(boolean success, T data) {
    this.success = success;
    this.data = data;
  }

  public OperationResponse(boolean success, T data, List<String> messages) {
    this.success = success;
    this.data = data;
    this.messages = messages;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public List<String> getMessages() {
    return messages;
  }

  public void setMessages(List<String> messages) {
    this.messages = messages;
  }

  public void addMessage(String message) {
    this.messages.add(message);
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public static class Builder<T> {
    private boolean success;
    private List<String> messages = new ArrayList<>();
    private T data;

    public Builder<T> success(boolean success) {
      this.success = success;
      return this;
    }

    public Builder<T> messages(List<String> messages) {
      this.messages = messages;
      return this;
    }

    public Builder<T> addMessage(String message) {
      this.messages.add(message);
      return this;
    }

    public Builder<T> data(T data) {
      this.data = data;
      return this;
    }

    public OperationResponse<T> build() {
      return new OperationResponse<>(success, data, messages);
    }
  }

  @Override
  public String toString() {
    return "OperationResponse{"
        + "success="
        + success
        + ", messages="
        + messages
        + ", data="
        + data
        + '}';
  }
}
