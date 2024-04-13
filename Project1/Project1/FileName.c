/*
 * ȭ�ϸ� : my_assembler_20222904.c
 * ��  �� : �� ���α׷��� SIC/XE �ӽ��� ���� ������ Assembler ���α׷���
 * ���η�ƾ����, �Էµ� ������ �ڵ� ��, ��ɾ �ش��ϴ� OPCODE�� ã��
 * ����Ѵ�. ���� ������ ���Ǵ� ���ڿ� "00000000"���� �ڽ��� �й��� �����Ѵ�.
 */

 /*
  * ���α׷��� ����� �����Ѵ�.
  */
#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>

  // ���ϸ��� "00000000"�� �ڽ��� �й����� ������ ��.
#include "my_assembler_20222904.h"

/* ------------------------------------------------------------
 * ���� : ����ڷ� ���� ����� ������ �޾Ƽ� ��ɾ��� OPCODE�� ã�� ����Ѵ�.
 * �Ű� : ���� ����, ����� ����
 * ��ȯ : ���� = 0, ���� = < 0
 * ���� : ���� ����� ���α׷��� ����Ʈ ������ �����ϴ� ��ƾ�� ������ �ʾҴ�.
 *        ���� �߰������� �������� �ʴ´�.
 * ------------------------------------------------------------ */
int main(int argc, char** argv) {
	// SIC/XE �ӽ��� instruction ������ �����ϴ� ���̺��̴�.
	inst* inst_table[MAX_INST_TABLE_LENGTH];
	int inst_table_length;

	// �ҽ��ڵ带 �����ϴ� ���̺��̴�. ���� ���� �����Ѵ�.
	char* input[MAX_INPUT_LINES];
	int input_length;

	// �ҽ��ڵ��� �� ������ ��ū���� ��ȯ�Ͽ� �����Ѵ�.
	token* tokens[MAX_INPUT_LINES];
	int tokens_length;

	// �ҽ��ڵ� ���� �ɺ��� �����ϴ� ���̺��̴�. ���� ������ ��� ����.
	symbol* symbol_table[MAX_TABLE_LENGTH];
	int symbol_table_length;

	// �ҽ��ڵ� ���� ���ͷ��� �����ϴ� ���̺��̴�. ���� ������ ��� ����.
	literal* literal_table[MAX_TABLE_LENGTH];
	int literal_table_length;

	// ������Ʈ �ڵ带 �����ϴ� ���̺��̴�. ���� ������ ��� ����.
	char object_code[MAX_OBJECT_CODE_LENGTH][MAX_OBJECT_CODE_STRING];
	int object_code_length;

	int err = 0;

	if ((err = init_inst_table(inst_table, &inst_table_length, "inst_table.txt")) < 0) {
		fprintf(stderr, "init_inst_table: ���� ��� �ʱ�ȭ�� �����߽��ϴ�. (error_code: %d)\n", err);
		return -1;
	}

	if ((err = init_input(input, &input_length, "input.txt")) < 0) {
		fprintf(stderr, "init_input: �ҽ��ڵ� �Է¿� �����߽��ϴ�. (error_code: %d)\n", err);
		return -1;
	}

	if ((err = assem_pass1((const inst**)inst_table, inst_table_length,
		(const char**)input, input_length, tokens,
		&tokens_length, symbol_table, &symbol_table_length,
		literal_table, &literal_table_length)) < 0) {
		fprintf(stderr, "assem_pass1: �н�1 �������� �����߽��ϴ�. (error_code: %d)\n", err);
		return -1;
	}

	if ((err = make_opcode_output("output_20222904.txt",
		(const token**)tokens,
		tokens_length,
		(const inst**)inst_table,
		inst_table_length)) < 0) {
		fprintf(stderr, "make_opcode_output: opcode ���� ��� �������� �����߽��ϴ�. (error_code: %d)\n", err);
		return -1;
	}

	// ���� ������Ʈ���� ���Ǵ� �κ�
	/*
	if ((err = make_symbol_table_output("symtab_00000000", (const symbol **)symbol_table, symbol_table_length)) < 0) {
		fprintf(stderr, "make_symbol_table_output: �ɺ����̺� ���� ��� �������� �����߽��ϴ�. (error_code: %d)\n", err);
		return -1;
	}

	if ((err = make_literal_table_output("littab_00000000", (const literal **)literal_table, literal_table_length)) < 0) {
		fprintf(stderr, "make_literal_table_output: ���ͷ����̺� ���� ��� �������� �����߽��ϴ�. (error_code: %d)\n", err);
		return -1;
	}

	if ((err = assem_pass2((const token **)tokens, tokens_length,
						   (const symbol **)symbol_table, symbol_table_length,
						   (const literal **)literal_table, literal_table_length,
						   object_code, &object_code_length)) < 0) {
		fprintf(stderr, "assem_pass2: �н�2 �������� �����߽��ϴ�. (error_code: %d)\n", err);
		return -1;
	}

	if ((err = make_objectcode_output("output_00000000",
									  (const char(*)[74])object_code,
									  object_code_length)) < 0) {
		fprintf(stderr, "make_objectcode_output: ������Ʈ�ڵ� ���� ��� �������� �����߽��ϴ�. (error_code: %d)\n", err);
		return -1;
	}
	*/

	return 0;
}

/* ----------------------------------------------------------------------------------
 * ���� : �ӽ��� ���� ��� �ڵ��� ������ �о� ���� ��� ���̺�(inst_table)��
 *        �����ϴ� �Լ��̴�.
 * �Ű� : ���� ��� ���̺�, ���� ��� ���̺� ����, ���� ��� ���ϸ�
 * ��ȯ : �������� = 0 , ���� < 0
 * ���� : ���� ������� ������ �����Ӱ� �����Ѵ�. ���ô� ������ ����.
 *
 *    ===============================================================================
 *           | �̸� | ���� | ���� �ڵ� | ���۷����� ���� | NULL|
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

		// ��ɾ� �̸�
		char* token = strtok(buf, ","); // �޸��� �������� ����
		if (token == NULL) continue;
		strcpy(inst_p->str, token);

		// ��ɾ� ����
		token = strtok(NULL, ",");
		if (token == NULL) continue;
		inst_p->format = atoi(token);

		// 16���� �ڵ�
		token = strtok(NULL, ",");
		if (token == NULL) continue;
		inst_p->op = (unsigned char)strtol(token, NULL, 16); // 16���� ���ڿ��� ����(unsigned char)�� ��ȯ

		// operand ����
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
 * ���� : ����� �� �ҽ��ڵ带 �о� �ҽ��ڵ� ���̺�(input_data)�� �����ϴ�
 *        �Լ��̴�.
 * �Ű� : �ҽ��� ������ �迭, �ҽ��� ������ �迭 ����, ������� �ҽ����ϸ�
 * ��ȯ : �������� = 0 , ���� < 0
 * ���� : ���δ����� �����Ѵ�.
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
		printf("%s\n", buf);
		char* line = (char*)malloc(sizeof(char) * strlen(buf));
		if (line == NULL) return -2;
		strcpy(line, buf);
		input[i] = line;
		fgetc(fp); // ���๮�� �б�
		i++;
	}
	*input_length = i;

	fclose(fp);

	return 0;
}

/* ----------------------------------------------------------------------------------
 * ���� : ����� �ڵ带 ���� �н�1 ������ �����ϴ� �Լ��̴�.
 *        �н�1������ ���α׷� �ҽ��� ��ĵ�Ͽ� �ش��ϴ� ��ū ������ �и��Ͽ�
 *        ���α׷� ���κ� ��ū ���̺��� �����Ѵ�.
 * �Ű� : ���� ��� ���̺�, ���� ��� ���̺� ����, �ҽ��� ����� �迭, �ҽ�
 *        ���� ����, ��ū�� ����� �迭, ��ū�� ����� �迭 ����, �ɺ� ���̺�,
 *        �ɺ� ���̺� ����, ���ͷ� ���̺�, ���ͷ� ���̺� ����
 * ��ȯ : ���� ���� = 0 , ���� = < 0
 * ���� : �ɺ� ���̺� �� ���ͷ� ���̺� ������ ���� �������� �����Ѵ�.
 * -----------------------------------------------------------------------------------
 */
int assem_pass1(const inst** inst_table, int inst_table_length,
	const char** input, int input_length, token** tokens,
	int* tokens_length, symbol** symbol_table,
	int* symbol_table_length, literal** literal_table,
	int* literal_table_length) {
	/* add your code here */

	/* input�� ���ڿ��� �� �پ� �Է� �޾Ƽ�
	 * token_parsing �Լ��� ȣ���Ͽ� tokens�� ����
	 */

	int token_size = 0, err;
	for (int i = 0; i < input_length; i++) {
		if (input[i][0] == '.') continue; // '.'���� �����ϴ� ������ �ּ����� �Ǵ�
		token* tok = (token*)malloc(sizeof(token));
		if (tok == NULL) return -2;
		if ((err = token_parsing(input[i], tok, NULL, NULL)) < 0) return err;
		tokens[token_size] = tok;
		token_size++;
	}
	*tokens_length = token_size;

	return 0;
}

/* ----------------------------------------------------------------------------------
 * ���� : �ҽ� �ڵ带 �о�� ��ū������ �м��ϰ� ��ū ���̺��� �ۼ��ϴ�
 *        �Լ��̴�. �н� 1�κ��� ȣ��ȴ�.
 * �Ű� : �Ľ��� ���ϴ� ���ڿ�, ����� ������ ��ū
 * ��ȯ : �������� = 0 , ���� < 0
 * ���� : my_assembler ���α׷��� ���� ������ ��ū �� ������Ʈ�� �����Ѵ�.
 * ----------------------------------------------------------------------------------
 */
int token_parsing(const char* input, token* tok, const inst* inst_table[],
	int inst_table_length) {
	/* add your code here */

	char* token = strtok(input, "\t");

	// operand �ʱ�ȭ
	for (int i = 0; i < MAX_OPERAND_PER_INST; i++)
		tok->operand[i] = NULL;

	// [���̺� ���� �ܰ�]
	if (input[0] == ' ' || input[0] == '\t') {
		// ���� ���ڷ� �����ϸ� ���̺��� �������� �ʴ´ٰ� �Ǵ�
		tok->label = NULL;
	}
	else {
		tok->label = (char*)malloc(sizeof(char) * strlen(token));
		if (tok->label == NULL) return -2;
		strcpy(tok->label, token);
		token = strtok(NULL, "\t"); // ���� ��ū ����
	}

	if (token == NULL) return -10;

	// [��ɾ� ���� �ܰ�]
	int op_len = strlen(token);
	if (op_len == 0) {
		tok->operator = NULL;
	}
	else {
		tok->operator = (char*)malloc(sizeof(char) * op_len);
		if (tok->operator == NULL) return -2;
		strcpy(tok->operator, token);
	}

	// [�ǿ����� ���� �ܰ�]
	token = strtok(NULL, "\t");
	char* comment = strtok(NULL, "\t");

	// �ǿ����ڰ� ���� ���.
	// �ǿ����ڰ� ���� �ٷ� �ڿ� comment�� ���� ���� ������� ����
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


	// [comment ���� �ܰ�]
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
 * ���� : �Է� ���ڿ��� ���� �ڵ������� �˻��ϴ� �Լ��̴�.
 * �Ű� : �Է� ���ڿ�, ���� ��� ���̺�, ���� ��� ���̺� ����
 * ��ȯ : �������� = ���� ���̺� �ε���, ���� < 0
 * ���� :
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
 * ���� : �ҽ��ڵ� ��ɾ� �տ� OPCODE�� ��ϵ� �ڵ带 ���Ͽ� ����ϴ� �Լ��̴�.
 *        ���⼭ ��µǴ� ������ ��ɾ� ���� OPCODE�� ��ϵ� ǥ(���� 3��)�̴�.
 * �Ű� : ������ ������Ʈ ���ϸ�, ��ū�� ����� �迭, ��ū �迭 ����
 * ��ȯ : �������� = 0, �����߻� = < 0
 * ���� : ���� ���ڷ� NULL���� ���´ٸ� ���α׷��� ����� ǥ��������� ������
 *        ȭ�鿡 ������ش�. ���� ���� 3�������� ���̴� �Լ��̹Ƿ� ������
 *        ������Ʈ������ ������ �ʴ´�.
 * -----------------------------------------------------------------------------------
 */
int make_opcode_output(const char* output_dir, const token** tokens,
	int tokens_length, const inst** inst_table,
	int inst_table_length) {
	FILE* fp;
	int stdout_flag = 0;

	/* add your code here */

	if (output_dir == NULL) {
		// dir�� NULL�̸� ǥ��������� ����
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
 * ���� : ����� �ڵ带 ���� �ڵ�� �ٲٱ� ���� �н�2 ������ �����ϴ�
 *        �Լ��̴�. �н� 2�� ���α׷��� ����� �ٲٴ� �۾��� ���� ������
 *        ����ȴ�.
 * �Ű� : ��ū �迭, ��ū �迭 ����, �ɺ� ���̺�, �ɺ� ���̺� ����, ���ͷ�
 *        ���̺�, ���ͷ� ���̺� ����, ������Ʈ �ڵ尡 ��� �迭, ������Ʈ �ڵ���
 *        ���� ����
 * ��ȯ : �������� = 0, �����߻� = < 0
 * ���� :
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
 * ���� : �Էµ� ���ڿ��� �̸��� ���� ���Ͽ� ���α׷��� ����� �����ϴ�
 *        �Լ��̴�. ���⼭ ��µǴ� ������ SYMBOL�� �ּҰ��� ����� TABLE�̴�.
 * �Ű� : ������ ���ϸ�, �ɺ� ���̺�, �ɺ� ���̺� ����
 * ��ȯ : �������� = 0, �����߻� = < 0
 * ���� : ���� ���ڷ� NULL���� ���´ٸ� ���α׷��� ����� ǥ��������� ������
 *        ȭ�鿡 ������ش�.
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
 * ���� : �Էµ� ���ڿ��� �̸��� ���� ���Ͽ� ���α׷��� ����� �����ϴ�
 *        �Լ��̴�. ���⼭ ��µǴ� ������ LITERAL�� �ּҰ��� ����� TABLE�̴�.
 * �Ű� : ������ ���ϸ�, ���ͷ� ���̺�, ���ͷ� ���̺� ����
 * ��ȯ : �������� = 0, �����߻� = < 0
 * ���� : ���� ���ڷ� NULL���� ���´ٸ� ���α׷��� ����� ǥ��������� ������
 *        ȭ�鿡 ������ش�.
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
 * ���� : �Էµ� ���ڿ��� �̸��� ���� ���Ͽ� ���α׷��� ����� �����ϴ�
 *        �Լ��̴�. ���⼭ ��µǴ� ������ object code�̴�.
 * �Ű� : ������ ���ϸ�, ������Ʈ �ڵ� �迭, ������Ʈ �ڵ� ���� ����
 * ��ȯ : �������� = 0, �����߻� = < 0
 * ���� : ���� ���ڷ� NULL���� ���´ٸ� ���α׷��� ����� ǥ��������� ������
 *        ȭ�鿡 ������ش�.
 *
 * -----------------------------------------------------------------------------------
 */
int make_objectcode_output(const char* objectcode_dir,
	const char object_code[][MAX_OBJECT_CODE_STRING],
	int object_code_length) {
	/* add your code here */

	return 0;
}
