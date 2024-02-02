package com.chensoul.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Event<ID, T> {
	public enum Type {
		CREATE,
		DELETE
	}

	private final Type eventType;
	private final ID id;
	private final T data;
	private final LocalDateTime createdTime = LocalDateTime.now();
}
