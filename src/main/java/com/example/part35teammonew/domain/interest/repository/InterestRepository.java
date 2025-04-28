package com.example.part35teammonew.domain.interest.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.part35teammonew.domain.interest.entity.Interest;

@Repository
public interface InterestRepository extends JpaRepository<Interest, UUID> {

	/**
	 * 전체 관심사 이름 조회
	 * @return 관심사 이름 리스트
	 */
	@Query("SELECT i.name FROM Interest i")
	List<String> findAllNames();

	/**
	 * 이름 또는 키워드에 부분 일치하는 관심사 검색
	 * @param pageable 페이징과 정렬을 파라미터로 전달
	 * @return Page<Interest> 형태로 전달 totalElements, totalPages 정보까지 함께 제공
	 */
	@Query(
		"SELECT i FROM Interest i " +
			" WHERE lower(i.name)    LIKE lower(concat('%', :search, '%'))" +
			"    OR lower(i.keywords) LIKE lower(concat('%', :search, '%'))"
	)
	Page<Interest> searchByNameOrKeyword(@Param("search") String search, Pageable pageable);

	/**
	 2) NAME 기준
	 */
	@Query("""
		SELECT i FROM Interest i
		 WHERE (:cursor IS NULL
		    OR i.name > :cursor
		    OR (i.name = :cursor AND i.createdAt > :after))
		 ORDER BY i.name   ASC, i.createdAt ASC
		""")
	List<Interest> findByNameAfter(
		@Param("cursor") String nameCursor,
		@Param("after") LocalDateTime after,
		Pageable pageable
	);

	/**
	 * SUBSCRIBER_COUNT 기준
	 */
	@Query("""
		SELECT i FROM Interest i
		 WHERE (:cursor IS NULL
		    OR i.subscriberCount > :cursor
		    OR (i.subscriberCount = :cursor AND i.createdAt > :after))
		 ORDER BY i.subscriberCount DESC, i.createdAt DESC
		""")
	List<Interest> findBySubscriberCountAfter(
		@Param("cursor") Long countCursor,
		@Param("after") LocalDateTime after,
		Pageable pageable
	);

	@Query("""
			SELECT COUNT(i) FROM Interest i
			WHERE lower(i.name) LIKE lower(concat('%', :keyword, '%'))
			   OR lower(i.keywords) LIKE lower(concat('%', :keyword, '%'))
		""")
	long countByNameOrKeyword(@Param("keyword") String keyword);

}
