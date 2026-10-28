package com.example.todoapp;

import java.util.List;
import java.time.LocalDate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TodoMapper {

	List<Todo> search(
			@Param("keyword") String keyword,
			@Param("category") String category,
			@Param("order") String order,
			@Param("showCompleted") boolean showCompleted,
			@Param("limit") int limit,
			@Param("offset") int offset);

	int count(
			@Param("keyword") String keyword,
			@Param("category") String category,
			@Param("showCompleted") boolean showCompleted);

	List<Todo> exportTodos(
			@Param("keyword") String keyword,
			@Param("category") String category,
			@Param("order") String order,
			@Param("showCompleted") boolean showCompleted);

	List<Todo> searchByDueDate(
			@Param("keyword") String keyword,
			@Param("category") String category,
			@Param("order") String order,
			@Param("from") LocalDate from,
			@Param("to") LocalDate to);

	void insert(Todo todo);

	Todo findById(Long id);

	void update(Todo todo);

	void updatePinned(@Param("id") Long id, @Param("pinned") boolean pinned);

	void deleteById(Long id);
}
