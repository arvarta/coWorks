package org.project.coWorks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="permission")
public class Permission {
/*		권한별 예시
 * 		읽기		: 1
 * 		신고		: 2
 * 		댓글		: 4
 * 		게시글	: 8
 * 		파일첨부	: 16
 * 		지도추가	: 32
 * 		투표추가	: 64
 * 		공지작성	: 128
 * 		각 권한 추가시 byte단위 연산으로 00000000~11111111(2^8개)까지 표현 가능
 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "permission_id", columnDefinition = "BIGINT")
	private Long permissionId;

	@Column(name = "permission_name", length = 50)
	private String permissionName;
}
