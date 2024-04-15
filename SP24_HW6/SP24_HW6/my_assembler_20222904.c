/*
 * 화일명 : my_assembler_20222904.c
 * 설  명 : 이 프로그램은 SIC/XE 머신을 위한 간단한 Assembler 프로그램의
 * 메인루틴으로, 입력된 파일의 코드 중, 명령어에 해당하는 OPCODE를 찾아
 * 출력한다. 파일 내에서 사용되는 문자열 "00000000"에는 자신의 학번을 기입한다.
 */

 /*
  * 프로그램의 헤더를 정의한다.
  */
#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>

  // 파일명의 "00000000"은 자신의 학번으로 변경할 것.
#include "my_assembler_20222904.h"

/* ------------------------------------------------------------
 * 설명 : 사용자로 부터 어셈블리 파일을 받아서 명령어의 OPCODE를 찾아 출력한다.
 * 매계 : 실행 파일, 어셈블리 파일
 * 반환 : 성공 = 0, 실패 = < 0
 * 주의 : 현재 어셈블리 프로그램의 리스트 파일을 생성하는 루틴은 만들지 않았다.
 *        또한 중간파일을 생성하지 않는다.
 * ------------------------------------------------------------ */
int main(int argc, char** argv) {
	// SIC/XE 머신의 instruction 정보를 저장하는 테이블이다.
	inst* inst_table[MAX_INST_TABLE_LENGTH];
	int inst_table_length;

	// 소스코드를 저장하는 테이블이다. 라인 단위 저장한다.
	char* input[MAX_INPUT_LINES];
	int input_length;

	// 소스코드의 각 라인을 토큰으로 전환하여 저장한다.
	token* tokens[MAX_INPUT_LINES];
	int tokens_length;

	// 소스코드 내의 심볼을 저장하는 테이블이다. 추후 과제에 사용 예정.
	symbol* symbol_table[MAX_TABLE_LENGTH];
	int symbol_table_length;

	// 소스코드 내의 리터럴을 저장하는 테이블이다. 추후 과제에 사용 예정.
	literal* literal_table[MAX_TABLE_LENGTH];
	int literal_table_length;

	// 오브젝트 코드를 저장하는 테이블이다. 추후 과제에 사용 예정.
	char object_code[MAX_OBJECT_CODE_LENGTH][MAX_OBJECT_CODE_STRING];
	int object_code_length;

	int err = 0;

	if ((err = init_inst_table(inst_table, &inst_table_length, "inst_table.txt")) < 0) {
		fprintf(stderr, "init_inst_table: 기계어 목록 초기화에 실패했습니다. (error_code: %d)\n", err);
		return -1;
	}

	if ((err = init_input(input, &input_length, "input.txt")) < 0) {
		fprintf(stderr, "init_input: 소스코드 입력에 실패했습니다. (error_code: %d)\n", err);
		return -1;
	}

	if ((err = assem_pass1((const inst**)inst_table, inst_table_length,
		(const char**)input, input_length, tokens,
		&tokens_length, symbol_table, &symbol_table_length,
		literal_table, &literal_table_length)) < 0) {
		fprintf(stderr, "assem_pass1: 패스1 과정에서 실패했습니다. (error_code: %d)\n", err);
		return -1;
	}

	if ((err = make_opcode_output("output_20222904.txt",
		(const token**)tokens,
		tokens_length,
		(const inst**)inst_table,
		inst_table_length)) < 0) {
		fprintf(stderr, "make_opcode_output: opcode 파일 출력 과정에서 실패했습니다. (error_code: %d)\n", err);
		return -1;
	}

	// 추후 프로젝트에서 사용되는 부분
	/*
	if ((err = make_symbol_table_output("symtab_00000000", (const symbol **)symbol_table, symbol_table_length)) < 0) {
		fprintf(stderr, "make_symbol_table_output: 심볼테이블 파일 출력 과정에서 실패했습니다. (error_code: %d)\n", err);
		return -1;
	}

	if ((err = make_literal_table_output("littab_00000000", (const literal **)literal_table, literal_table_length)) < 0) {
		fprintf(stderr, "make_literal_table_output: 리터럴테이블 파일 출력 과정에서 실패했습니다. (error_code: %d)\n", err);
		return -1;
	}

	if ((err = assem_pass2((const token **)tokens, tokens_length,
						   (const symbol **)symbol_table, symbol_table_length,
						   (const literal **)literal_table, literal_table_length,
						   object_code, &object_code_length)) < 0) {
		fprintf(stderr, "assem_pass2: 패스2 과정에서 실패했습니다. (error_code: %d)\n", err);
		return -1;
	}

	if ((err = make_objectcode_output("output_00000000",
									  (const char(*)[74])object_code,
									  object_code_length)) < 0) {
		fprintf(stderr, "make_objectcode_output: 오브젝트코드 파일 출력 과정에서 실패했습니다. (error_code: %d)\n", err);
		return -1;
	}
	*/

	return 0;
}

/* ----------------------------------------------------------------------------------
 * 설명 : 머신을 위한 기계 코드목록 파일을 읽어 기계어 목록 테이블(inst_table)을
 *        생성하는 함수이다.
 * 매계 : 기계어 목록 테이블, 기계어 목록 테이블 길이, 기계어 목록 파일명
 * 반환 : 정상종료 = 0 , 에러 < 0
 * 주의 : 기계어 목록파일 형식은 자유롭게 구현한다. 예시는 다음과 같다.
 *
 *    ===============================================================================
 *           | 이름 | 형식 | 기계어 코드 | 오퍼랜드의 갯수 | NULL|
 *    ===============================================================================
 *
 * ----------------------------------------------------------------------------------
 */
int init_inst_table(inst** inst_table, int* inst_table_length,
	const char* inst_table_dir) {
	FILE* fp;
	char buf[100] = { 0, };
	int i = 0;

	/* add your code here */

	if ((fp = fopen(inst_table_dir, "r")) == NULL) {
		return -1;
	}

	fseek(fp, 0, SEEK_SET);

	while (1) {
		if (EOF == fscanf(fp, "%s", buf))
			break;

		inst* inst_p = (inst*)malloc(sizeof(inst));
		if (inst_p == NULL) return -2;

		// 명령어 이름
		char* token = strtok(buf, ","); // 콤마를 기준으로 구분
		if (token == NULL) continue;
		strcpy(inst_p->str, token);

		// 명령어 형식
		token = strtok(NULL, ",");
		if (token == NULL) continue;
		inst_p->format = atoi(token);

		// 16진수 코드
		token = strtok(NULL, ",");
		if (token == NULL) continue;
		inst_p->op = (unsigned char)strtol(token, NULL, 16); // 16진수 문자열을 정수(unsigned char)로 변환

		// operand 개수
		token = strtok(NULL, ",");
		if (token == NULL) continue;
		inst_p->ops = atoi(token);

		inst_table[i] = inst_p;
		i++;
	}
	*inst_table_length = i;

	fclose(fp);
	return 0;
}

/* ----------------------------------------------------------------------------------
 * 설명 : 어셈블리 할 소스코드를 읽어 소스코드 테이블(input_data)를 생성하는
 *        함수이다.
 * 매계 : 소스를 저장할 배열, 소스를 저장할 배열 길이, 어셈블리할 소스파일명
 * 반환 : 정상종료 = 0 , 에러 < 0
 * 주의 : 라인단위로 저장한다.
 * ----------------------------------------------------------------------------------
 */
int init_input(char** input, int* input_length, const char* input_dir) {
	FILE* fp;
	int i = 0;

	/* add your code here */

	if ((fp = fopen(input_dir, "r")) == NULL) {
		return -1;
	}

	fseek(fp, 0, SEEK_SET);

	char buf[1000] = { 0, };
	while (EOF != fscanf(fp, "%[^\n]s", buf)) {
		char* line = (char*)malloc(sizeof(char) * strlen(buf));
		if (line == NULL) return -2;
		strcpy(line, buf);
		input[i] = line;
		fgetc(fp); // 개행문자 읽기
		i++;
	}
	*input_length = i;

	fclose(fp);

	return 0;
}

/* ----------------------------------------------------------------------------------
 * 설명 : 어셈블리 코드를 위한 패스1 과정을 수행하는 함수이다.
 *        패스1에서는 프로그램 소스를 스캔하여 해당하는 토큰 단위로 분리하여
 *        프로그램 라인별 토큰 테이블을 생성한다.
 * 매계 : 기계어 목록 테이블, 기계어 목록 테이블 길이, 소스가 저장된 배열, 소스
 *        라인 길이, 토큰이 저장될 배열, 토큰이 저장될 배열 길이, 심볼 테이블,
 *        심볼 테이블 길이, 리터럴 테이블, 리터럴 테이블 길이
 * 반환 : 정상 종료 = 0 , 에러 = < 0
 * 주의 : 심볼 테이블 및 리터럴 테이블 설정은 추후 과제에서 수행한다.
 * -----------------------------------------------------------------------------------
 */
int assem_pass1(const inst** inst_table, int inst_table_length,
	const char** input, int input_length, token** tokens,
	int* tokens_length, symbol** symbol_table,
	int* symbol_table_length, literal** literal_table,
	int* literal_table_length) {
	/* add your code here */

	/* input의 문자열을 한 줄씩 입력 받아서
	 * token_parsing 함수를 호출하여 tokens에 저장
	 */

	int token_size = 0, err;
	for (int i = 0; i < input_length; i++) {
		if (input[i][0] == '.') continue; // '.'으로 시작하는 라인은 주석으로 판단
		token* tok = (token*)malloc(sizeof(token));
		if (tok == NULL) return -2;
		if ((err = token_parsing(input[i], tok)) < 0) return err;
		tokens[token_size] = tok;
		token_size++;
	}
	*tokens_length = token_size;

	return 0;
}

/* ----------------------------------------------------------------------------------
 * 설명 : 소스 코드를 읽어와 토큰단위로 분석하고 토큰 테이블을 작성하는
 *        함수이다. 패스 1로부터 호출된다.
 * 매계 : 파싱을 원하는 문자열, 결과를 저장할 토큰
 * 반환 : 정상종료 = 0 , 에러 < 0
 * 주의 : my_assembler 프로그램은 라인 단위로 토큰 및 오브젝트를 관리한다.
 * ----------------------------------------------------------------------------------
 */
int token_parsing(const char* input, token* tok) {
	/* add your code here */

	char* token = strtok(input, "\t");

	// operand 초기화
	for (int i = 0; i < MAX_OPERAND_PER_INST; i++)
		tok->operand[i] = NULL;

	// [레이블 복사 단계]
	if (input[0] == ' ' || input[0] == '\t') {
		// 공백 문자로 시작하면 레이블이 존재하지 않는다고 판단
		tok->label = NULL;
	}
	else {
		tok->label = (char*)malloc(sizeof(char) * strlen(token));
		if (tok->label == NULL) return -2;
		strcpy(tok->label, token);
		token = strtok(NULL, "\t"); // 다음 토큰 받음
	}

	if (token == NULL) return -10;

	// [명령어 복사 단계]
	int op_len = strlen(token);
	if (op_len == 0) {
		tok->operator = NULL;
	}
	else {
		tok->operator = (char*)malloc(sizeof(char) * op_len);
		if (tok->operator == NULL) return -2;
		strcpy(tok->operator, token);
	}

	// [피연산자 복사 단계]
	token = strtok(NULL, "\t");
	char* comment = strtok(NULL, "\t");

	// 피연산자가 없는 경우.
	// 피연산자가 없고 바로 뒤에 comment가 오는 경우는 고려하지 않음
	if (token == NULL) return 0;
	int opnd_len = strlen(token);
	int opnd_cnt = 0;
	char opnd_buf[30] = { 0, };
	strcpy(opnd_buf, token);

	char* opnd_tok = strtok(opnd_buf, ",");
	while (opnd_tok) {
		char* opnd = (char*)malloc(sizeof(char) * strlen(opnd_tok));
		if (opnd == NULL) return -2;
		strcpy(opnd, opnd_tok);
		tok->operand[opnd_cnt] = opnd;
		opnd_cnt++;
		opnd_tok = strtok(NULL, ",");
	}


	// [comment 복사 단계]
	if (comment == NULL) return 0;
	int cmt_len = strlen(comment);
	if (cmt_len == 0) {
		tok->comment = NULL;
	}
	else {
		char* cmt = (char*)malloc(sizeof(char) * strlen(comment));
		if (cmt == NULL) return -2;
		strcpy(cmt, comment);
		tok->comment = cmt;
	}

	return 0;
}

/* ----------------------------------------------------------------------------------
 * 설명 : 입력 문자열이 기계어 코드인지를 검사하는 함수이다.
 * 매계 : 입력 문자열, 기계어 목록 테이블, 기계어 목록 테이블 길이
 * 반환 : 정상종료 = 기계어 테이블 인덱스, 에러 < 0
 * 주의 :
 * ----------------------------------------------------------------------------------
 */
int search_opcode(const char* str, const inst** inst_table,
	int inst_table_length) {
	/* add your code here */
	int offset = str[0] == '+' ? 1 : 0;

	for (int i = 0; i < inst_table_length; i++) {
		if (strcmp(str + offset, inst_table[i]->str) == 0)
			return i;
	}

	return -1;
}

/* ----------------------------------------------------------------------------------
 * 설명 : 소스코드 명령어 앞에 OPCODE가 기록된 코드를 파일에 출력하는 함수이다.
 *        여기서 출력되는 내용은 명령어 옆에 OPCODE가 기록된 표(과제 3번)이다.
 * 매계 : 생성할 오브젝트 파일명, 토큰이 저장된 배열, 토큰 배열 길이
 * 반환 : 정상종료 = 0, 에러발생 = < 0
 * 주의 : 만약 인자로 NULL값이 들어온다면 프로그램의 결과를 표준출력으로 보내어
 *        화면에 출력해준다. 또한 과제 3번에서만 쓰이는 함수이므로 이후의
 *        프로젝트에서는 사용되지 않는다.
 * -----------------------------------------------------------------------------------
 */
int make_opcode_output(const char* output_dir, const token** tokens,
	int tokens_length, const inst** inst_table,
	int inst_table_length) {
	FILE* fp;
	int stdout_flag = 0;

	/* add your code here */

	if (output_dir == NULL) {
		// dir이 NULL이면 표준출력으로 지정
		stdout_flag = 1;
		fp = stdout;
	}
	else if ((fp = fopen(output_dir, "w")) == NULL) {
		return -1;
	}

	for (int i = 0; i < tokens_length; i++) {
		token* tok = tokens[i];
		if (tok->label != NULL) fprintf(fp, "%s", tok->label);
		if (tok->operator != NULL) fprintf(fp, "\t%s\t", tok->operator);

		for (int j = 0; j < MAX_OPERAND_PER_INST && tok->operand[j] != NULL; j++) {
			char* sep = (j + 1 == MAX_OPERAND_PER_INST || tok->operand[j + 1] == NULL) ? "" : ",";
			fprintf(fp, "%s%s", tok->operand[j], sep);
		}
		int inst_idx;
		if ((inst_idx = search_opcode(tok->operator, inst_table, inst_table_length)) >= 0) {
			fprintf(fp, "\t%X", inst_table[inst_idx]->op);
		}
		fprintf(fp, "\n");
	}

	if (stdout_flag == 0) fclose(fp);

	return 0;
}

/* ----------------------------------------------------------------------------------
 * 설명 : 어셈블리 코드를 기계어 코드로 바꾸기 위한 패스2 과정을 수행하는
 *        함수이다. 패스 2의 프로그램을 기계어로 바꾸는 작업은 라인 단위로
 *        수행된다.
 * 매계 : 토큰 배열, 토큰 배열 길이, 심볼 테이블, 심볼 테이블 길이, 리터럴
 *        테이블, 리터럴 테이블 길이, 오브젝트 코드가 담길 배열, 오브젝트 코드의
 *        라인 길이
 * 반환 : 정상종료 = 0, 에러발생 = < 0
 * 주의 :
 * -----------------------------------------------------------------------------------
 */
int assem_pass2(const token** tokens, int tokens_length,
	const symbol** symbol_table, int symbol_table_length,
	const literal** literal_table, int literal_table_length,
	char object_code[][MAX_OBJECT_CODE_STRING],
	int* object_code_length) {
	/* add your code here */

	return 0;
}

/* ----------------------------------------------------------------------------------
 * 설명 : 입력된 문자열의 이름을 가진 파일에 프로그램의 결과를 저장하는
 *        함수이다. 여기서 출력되는 내용은 SYMBOL별 주소값이 저장된 TABLE이다.
 * 매계 : 생성할 파일명, 심볼 테이블, 심볼 테이블 길이
 * 반환 : 정상종료 = 0, 에러발생 = < 0
 * 주의 : 만약 인자로 NULL값이 들어온다면 프로그램의 결과를 표준출력으로 보내어
 *        화면에 출력해준다.
 *
 * -----------------------------------------------------------------------------------
 */
int make_symbol_table_output(const char* symtab_dir,
	const symbol** symbol_table,
	int symbol_table_length) {
	/* add your code here */

	return 0;
}

/* ----------------------------------------------------------------------------------
 * 설명 : 입력된 문자열의 이름을 가진 파일에 프로그램의 결과를 저장하는
 *        함수이다. 여기서 출력되는 내용은 LITERAL별 주소값이 저장된 TABLE이다.
 * 매계 : 생성할 파일명, 리터럴 테이블, 리터럴 테이블 길이
 * 반환 : 정상종료 = 0, 에러발생 = < 0
 * 주의 : 만약 인자로 NULL값이 들어온다면 프로그램의 결과를 표준출력으로 보내어
 *        화면에 출력해준다.
 *
 * -----------------------------------------------------------------------------------
 */
int make_literal_table_output(const char* literal_table_dir,
	const literal** literal_table,
	int literal_table_length) {
	/* add your code here */

	return 0;
}

/* ----------------------------------------------------------------------------------
 * 설명 : 입력된 문자열의 이름을 가진 파일에 프로그램의 결과를 저장하는
 *        함수이다. 여기서 출력되는 내용은 object code이다.
 * 매계 : 생성할 파일명, 오브젝트 코드 배열, 오브젝트 코드 라인 길이
 * 반환 : 정상종료 = 0, 에러발생 = < 0
 * 주의 : 만약 인자로 NULL값이 들어온다면 프로그램의 결과를 표준출력으로 보내어
 *        화면에 출력해준다.
 *
 * -----------------------------------------------------------------------------------
 */
int make_objectcode_output(const char* objectcode_dir,
	const char object_code[][MAX_OBJECT_CODE_STRING],
	int object_code_length) {
	/* add your code here */

	return 0;
}
