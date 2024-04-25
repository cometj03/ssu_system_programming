/**
 * @file my_assembler_20222904.c
 * @date 2024-04-09
 * @version 0.1.0
 *
 * @brief SIC/XE 소스코드를 object code로 변환하는 프로그램
 *
 * @details
 * SIC/XE 소스코드를 해당 머신에서 동작하도록 object code로 변환하는
 * 프로그램이다. 파일 내에서 사용되는 문자열 "00000000"에는 자신의 학번을
 * 기입한다.
 */
#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* 파일명의 "00000000"은 자신의 학번으로 변경할 것 */
#include "my_assembler_20222904.h"

/**
 * @brief 사용자로부터 SIC/XE 소스코드를 받아서 object code를 출력한다.
 *
 * @details
 * 사용자로부터 SIC/XE 소스코드를 받아서 object code를 출력한다. 특별한 사유가
 * 없는 한 변경하지 말 것.
 */
int main(int argc, char **argv) {
    /** SIC/XE 머신의 instruction 정보를 저장하는 테이블 */
    inst *inst_table[MAX_INST_TABLE_LENGTH];
    int inst_table_length;

    /** SIC/XE 소스코드를 저장하는 테이블 */
    char *input[MAX_INPUT_LINES];
    int input_length;

    /** 소스코드의 각 라인을 토큰 전환하여 저장하는 테이블 */
    token *tokens[MAX_INPUT_LINES];
    int tokens_length;

    /** 소스코드 내의 심볼을 저장하는 테이블 */
    symbol *symbol_table[MAX_TABLE_LENGTH];
    int symbol_table_length;

    /** 소스코드 내의 리터럴을 저장하는 테이블 */
    literal *literal_table[MAX_TABLE_LENGTH];
    int literal_table_length;

    /** 오브젝트 코드를 저장하는 변수 */
    object_code *obj_code = NULL;

    int err = 0;

    if ((err = init_inst_table(inst_table, &inst_table_length,
                               "inst_table.txt")) < 0) {
        fprintf(stderr,
                "init_inst_table: 기계어 목록 초기화에 실패했습니다. "
                "(error_code: %d)\n",
                err);
        return -1;
    }

    if ((err = init_input(input, &input_length, "input.txt")) < 0) {
        fprintf(stderr,
                "init_input: 소스코드 입력에 실패했습니다. (error_code: %d)\n",
                err);
        return -1;
    }

    if ((err = assem_pass1((const inst **)inst_table, inst_table_length,
                           (const char **)input, input_length, tokens,
                           &tokens_length, symbol_table, &symbol_table_length,
                           literal_table, &literal_table_length)) < 0) {
        fprintf(stderr,
                "assem_pass1: 패스1 과정에서 실패했습니다. (error_code: %d)\n",
                err);
        return -1;
    }

    /** 프로젝트1에서는 불필요함 */
    /*
    if ((err = make_opcode_output("output_opcode.txt", (const token **)tokens,
                                  tokens_length, (const inst **)inst_table,
                                  inst_table_length)) < 0) {
        fprintf(stderr,
                "make_opcode_output: opcode 파일 출력 과정에서 실패했습니다. "
                "(error_code: %d)\n",
                err);
        return -1;
    }
    */

    if ((err = make_symbol_table_output("output_symtab.txt",
                                        (const symbol **)symbol_table,
                                        symbol_table_length)) < 0) {
        fprintf(stderr,
                "make_symbol_table_output: 심볼테이블 파일 출력 과정에서 "
                "실패했습니다. (error_code: %d)\n",
                err);
        return -1;
    }

    if ((err = make_literal_table_output("output_littab.txt",
                                         (const literal **)literal_table,
                                         literal_table_length)) < 0) {
        fprintf(stderr,
                "make_literal_table_output: 리터럴테이블 파일 출력 과정에서 "
                "실패했습니다. (error_code: %d)\n",
                err);
        return -1;
    }

    if ((err = assem_pass2((const token **)tokens, tokens_length,
                           (const inst **)inst_table, inst_table_length,
                           (const symbol **)symbol_table, symbol_table_length,
                           (const literal **)literal_table,
                           literal_table_length, obj_code)) < 0) {
        fprintf(stderr,
                "assem_pass2: 패스2 과정에서 실패했습니다. (error_code: %d)\n",
                err);
        return -1;
    }

    if ((err = make_objectcode_output("output_objectcode.txt",
                                      (const object_code *)obj_code)) < 0) {
        fprintf(stderr,
                "make_objectcode_output: 오브젝트코드 파일 출력 과정에서 "
                "실패했습니다. (error_code: %d)\n",
                err);
        return -1;
    }

    return 0;
}

/**
 * @brief 기계어 목록 파일(inst_table.txt)을 읽어 기계어 목록
 * 테이블(inst_table)을 생성한다.
 *
 * @param inst_table 기계어 목록 테이블의 시작 주소
 * @param inst_table_length 기계어 목록 테이블의 길이를 저장하는 변수 주소
 * @param inst_table_dir 기계어 목록 파일 경로
 * @return 오류 코드 (정상 종료 = 0)
 *
 * @details
 * 기계어 목록 파일(inst_table.txt)을 읽어 기계어 목록 테이블(inst_table)을
 * 생성한다. 기계어 목록 파일 형식은 자유롭게 구현한다. 예시는 다음과 같다.
 *    ==============================================================
 *           | 이름 | 형식 | 기계어 코드 | 오퍼랜드의 갯수 | \n |
 *    ==============================================================
 */
int init_inst_table(inst *inst_table[], int *inst_table_length,
                    const char *inst_table_dir) {
    FILE* fp;
    inst* ins;
    char buffer[20], str[10] = { 0 }, opcode[10] = { 0 };

    if ((fp = fopen(inst_table_dir, "rb")) == NULL) {
        return -1;
    }

    *inst_table_length = 0;

    while (!feof(fp)) {
        fgets(buffer, 20, fp);

        if ((ins = (inst*)malloc(sizeof(inst))) == NULL) return -2;
        sscanf(buffer, "%s %d %d %s", str, &ins->ops, &ins->format, opcode);
        strcpy(ins->str, str);
        ins->op = (unsigned char)strtol(opcode, NULL, 16); // 16진수 문자열을 정수(unsigned char)로 변환
        inst_table[*inst_table_length] = ins;
        ++(*inst_table_length);
    }

    fclose(fp);
    return 0;
}

/**
 * @brief SIC/XE 소스코드 파일(input.txt)을 읽어 소스코드 테이블(input)을
 * 생성한다.
 *
 * @param input 소스코드 테이블의 시작 주소
 * @param input_length 소스코드 테이블의 길이를 저장하는 변수 주소
 * @param input_dir 소스코드 파일 경로
 * @return 오류 코드 (정상 종료 = 0)
 */
int init_input(char *input[], int *input_length, const char *input_dir) {
    FILE* fp;
    char buffer[250] = { 0, }, *line;
    
    if ((fp = fopen(input_dir, "rb")) == NULL) {
        return -1;
    }

    *input_length = 0;

    while (!feof(fp)) {
        fgets(buffer, 249, fp);
        if ((line = (char*)malloc(strlen(buffer) + 1)) == NULL) {
            fclose(fp);
            return -2;
        }
        sscanf(buffer, "%[^\r^\n]", line);
        input[*input_length] = line;
        ++(*input_length);
    }

    fclose(fp);

    return 0;
}

/**
 * label이 유효한지 체크하는 함수입니다.
 * - label의 길이는 6보다 작거나 같아야 합니다.
 * - symbol table에 이미 같은 label이 존재하면 안 됩니다.
 * 
 * @return 오류: < 0, 정상 종료 == 0
 */
static int check_symbol_valid(const char* label, int line_number,
                              symbol* symbol_table[], int* symbol_table_length) {
    if (label == NULL) return -1;

    if (strlen(label) > 6) {
        fprintf(stderr, "line #%d : label '%s' is too long. label's length must be <= 6\n", line_number, label);
        return -5; // exceed symbol length
    }

    // symbol table에 존재하는지 확인
    // 존재한다면 에러
    for (int s = 0; s < *symbol_table_length; s++) {
        if (strcmp(symbol_table[s]->name, label) == 0) {
            fprintf(stderr, "line #%d : symbol '%s' is duplicated\n", line_number, label);
            return -3; // duplicated symbol
        }
    }

    return 0;
}

/**
 * symbol 인스턴스를 생성하여 symbol_table의 마지막에 삽입합니다.
 */
static int insert_label_into_symtbl(const char* label, const int* locctr, const char* csect_name,
                                    symbol* symbol_table[], int* symbol_table_length) {
    if (label == NULL) return -1;

    symbol* sb;
    if ((sb = (symbol*)malloc(sizeof(symbol))) == NULL) return -2;

    sb->addr = *locctr;
    strcpy(sb->name, label);
    if (csect_name != NULL) {
        strcpy(sb->csect_name, csect_name);
        sb->rflag = 1;
    }
    else {
        sb->csect_name[0] = '\0';
        sb->rflag = 0;
    }
    symbol_table[*symbol_table_length] = sb;
    ++(*symbol_table_length);

    return 0;
}

/**
 * literal 인스턴스를 생성하여 literal_table의 마지막에 삽입합니다.
 */
static int insert_literal_into_littbl(const char* literal_str, literal* literal_table[], int* literal_table_length) {
    if (literal_str == NULL) return -1;

    // 중복되는 리터럴은 삽입할 필요 없음
    for (int i = 0; i < *literal_table_length; i++) {
        if (strcmp(literal_table[i]->literal, literal_str) == 0)
            return 0;
    }

    literal* lit;
    if ((lit = (literal*)malloc(sizeof(literal))) == NULL) return -2;
    lit->addr = 0;
    strcpy(lit->literal, literal_str);
    literal_table[*literal_table_length] = lit;
    ++(*literal_table_length);
    return 0;
}

/**
 * @param lit_last_idx 주소가 지정되지 않은 리터럴의 첫 번째 인덱스
 * @param locctr 현재 위치의 location counter
 * 
 * @details
 * location counter를 적절히 증가시키면서 리터럴 테이블의 리터럴의 주소를 할당해줍니다.
 */
static int set_literal_addr(int* lit_last_idx, int* locctr, 
                            literal* literal_table[], const int* literal_table_length) {
    // lit_last_idx == literal_table_length이면 모든 리터럴의 주소가 지정되었다는 의미
    if (*lit_last_idx == *literal_table_length) return 0;

    while (*lit_last_idx < *literal_table_length) {
        literal* lit = literal_table[*lit_last_idx];
        lit->addr = *locctr;

        int lit_len = strlen(lit->literal);
        switch (lit->literal[1]) {
        case 'C':
            if (lit->literal[2] != '\'' || lit->literal[lit_len - 1] != '\'')
                return -10; // wrong literal format
            *locctr += lit_len - 3;
            break;
        case 'X':
            if (lit->literal[2] != '\'' || lit->literal[lit_len - 1] != '\'')
                return -10; // wrong literal format
            *locctr += (lit_len - 3 + 1) / 2;
            break;
        default:
            *locctr += 3; // WORD
            break;
        }
        ++(*lit_last_idx);
    }
    return 0;
}

/**
 * @brief 어셈블리 코드을 위한 패스 1 과정을 수행한다.
 *
 * @param inst_table 기계어 목록 테이블의 주소
 * @param inst_table_length 기계어 목록 테이블의 길이
 * @param input 소스코드 테이블의 주소
 * @param input_length 소스코드 테이블의 길이
 * @param tokens 토큰 테이블의 시작 주소
 * @param tokens_length 토큰 테이블의 길이를 저장하는 변수 주소
 * @param symbol_table 심볼 테이블의 시작 주소
 * @param symbol_table_length 심볼 테이블의 길이를 저장하는 변수 주소
 * @param literal_table 리터럴 테이블의 시작 주소
 * @param literal_table_length 리터럴 테이블의 길이를 저장하는 변수 주소
 * @return 오류 코드 (정상 종료 = 0)
 *
 * @details
 * 어셈블리 코드를 위한 패스1 과정을 수행하는 함수이다. 패스 1에서는 프로그램
 * 소스를 스캔하여 해당하는 토큰 단위로 분리하여 프로그램 라인별 토큰 테이블을
 * 생성한다. 토큰 테이블은 token_parsing 함수를 호출하여 설정하여야 한다. 또한,
 * assem_pass2 과정에서 사용하기 위한 심볼 테이블 및 리터럴 테이블을 생성한다.
 */
int assem_pass1(const inst *inst_table[], int inst_table_length,
                const char *input[], int input_length, 
                token *tokens[], int *tokens_length, 
                symbol *symbol_table[], int *symbol_table_length, 
                literal *literal_table[], int *literal_table_length) {
    FILE* fp = stdout;
    int locctr = 0; // location counter
    int err;
    char* csect_name = NULL; // current control section name
    token* tok;

    // 주소가 지정되지 않은 리터럴의 첫 번째 인덱스 
    // (lit_last_idx == *literal_table_length이면 테이블의 모든 리터럴이 주소가 지정되었다는 의미)
    int lit_last_idx = 0;

    *tokens_length = 0;
    *symbol_table_length = 0;
    *literal_table_length = 0;

    for (int i = 0; i < input_length; i++) {
        if (input[i][0] == '.') continue; // '.'으로 시작하는 라인은 주석으로 판단
        if ((tok = (token*)malloc(sizeof(token))) == NULL) return -2;

        // Parsing
        if ((err = token_parsing(input[i], tok, inst_table, inst_table_length)) < 0) return err;
        tokens[*tokens_length] = tok;
        ++(*tokens_length);

        // 레이블 처리
        if (tok->label != NULL) {
            if ((err = check_symbol_valid(tok->label, i, symbol_table, symbol_table_length)) < 0) return err;
            if ((err = insert_label_into_symtbl(tok->label, &locctr, csect_name, symbol_table, symbol_table_length)) < 0) return err;
        }

        // 리터럴 삽입
        if (tok->operand[0] != NULL && tok->operand[0][0] == '=') {
            if ((err = insert_literal_into_littbl(tok->operand[0], literal_table, literal_table_length)) < 0) return err;
        }

        // 명령어 처리
        int inst_idx = search_opcode(tok->operator, inst_table, inst_table_length);
        if (inst_idx == -1) return -4; // unknown operator

        inst* ins = inst_table[inst_idx];
        if (ins->format == 0) {
            // 어셈블러 지시어 (format == 0)
            if (strcmp(tok->operator, "START") == 0 && tok->operand[0] != NULL) {
                locctr = atoi(tok->operand[0]); // init
                csect_name = tok->label;
                if ((err = insert_label_into_symtbl(tok->label, &locctr, NULL, symbol_table, symbol_table_length)) < 0) return err;
            }
            else if (strcmp(tok->operator, "CSECT") == 0) {
                if ((err = set_literal_addr(&lit_last_idx, &locctr, literal_table, literal_table_length)) < 0) return err;
                locctr = 0;
                csect_name = tok->label;
            }
            else if (strcmp(tok->operator, "END") == 0) {
                break;
            }
            else if (strcmp(tok->operator, "EXTDEF") == 0) {
                // TODO
            }
            else if (strcmp(tok->operator, "EXTREF") == 0) {
                // TODO
            }
            else if (strcmp(tok->operator, "EQU") == 0) {
                // TODO
            }
            else if (strcmp(tok->operator, "LTORG") == 0) {
                if ((err = set_literal_addr(&lit_last_idx, &locctr, literal_table, literal_table_length)) < 0) return err;
            }
            else if (strcmp(tok->operator, "RESW") == 0 && tok->operand[0] != NULL) {
                locctr += atoi(tok->operand[0]) * 3;
            }
            else if (strcmp(tok->operator, "RESB") == 0 && tok->operand[0] != NULL) {
                locctr += atoi(tok->operand[0]);
            }
            else if (strcmp(tok->operator, "WORD") == 0) {
                locctr += 3;
            }
            else if (strcmp(tok->operator, "BYTE") == 0 && tok->operand[0] != NULL) {
                switch (tok->operand[0][0]) {
                case 'C':
                    locctr += strlen(tok->operand[0]) - 3; // C, 따옴표 2개 총 3개 제외
                    break;
                case 'X':
                    locctr += (strlen(tok->operand[0]) - 3 + 1) / 2; // X, 따옴표 2개 총 3개 제외 (두 문자 당 1byte)
                    break;
                }
            }
            else {
                // error: Unknown Directive
                return -4;
            }
        }
        else {
            locctr += inst_table[inst_idx]->format;
            if (tok->operator[0] == '+') locctr++;
        }
    }
    if ((err = set_literal_addr(&lit_last_idx, &locctr, literal_table, literal_table_length)) < 0) return err;
    // TODO: 중간 파일 생성
    return 0;
}

/**
 * @brief 한 줄의 소스코드를 파싱하여 토큰에 저장한다.
 *
 * @param input 파싱할 소스코드 문자열
 * @param tok 결과를 저장할 토큰 구조체 주소
 * @param inst_table 기계어 목록 테이블의 주소
 * @param inst_table_length 기계어 목록 테이블의 길이
 * @return 오류 코드 (정상 종료 = 0)
 */
int token_parsing(const char *input, token *tok, 
                  const inst *inst_table[], int inst_table_length) {
    tok->label = NULL;
    tok->operator = NULL;
    tok->comment = NULL;
    for (int i = 0; i < MAX_OPERAND_PER_INST; i++)
        tok->operand[i] = NULL;
    tok->nixbpe = 0;

    char label[10] = { 0 }, opr[10] = { 0 }, opnd[100] = { 0 }, comment[100] = { 0 };
    
    if (input[0] == '\t' || input[0] == ' ') {
        // 공백 문자로 시작하면 레이블이 존재하지 않는다고 판단
        sscanf(input, "\t%[^\t]\t%[^\t]\t%[^\0]", opr, opnd, comment);
    }
    else {
        sscanf(input, "%[^\t]\t%[^\t]\t%[^\t]\t%[^\0]", label, opr, opnd, comment);
        if ((tok->label = (char*)malloc(strlen(label) + 1)) == NULL) return -2;
        strcpy(tok->label, label);
    }

    // [명령어 복사 단계]
    if (opr[0] != '\0') {
        if ((tok->operator = (char*)malloc(strlen(opr) + 1)) == NULL) return -2;
        strcpy(tok->operator, opr);
    }

    // [comment 복사 단계]    
    if (comment[0] != '\0') {
        if ((tok->comment = (char*)malloc(strlen(comment) + 1)) == NULL) return -2;
        strcpy(tok->comment, comment);
    }

    // [operand 복사 단계]
    int opnd_cnt = 0;
    for (int st = 0; opnd[st] != '\0' && opnd_cnt < MAX_OPERAND_PER_INST; st++) {
        int end = st;
        while (opnd[end] != ',' && opnd[end] != '\0' && opnd[end] != '\t') end++;
        int len = end - st;
        if ((tok->operand[opnd_cnt] = (char*)malloc(len + 1)) == NULL) return -2;
        memcpy(tok->operand[opnd_cnt], opnd + st, len);
        tok->operand[opnd_cnt][len] = '\0';
        ++opnd_cnt;
        st = end;
    }

    // [nixbpe]
    int inst_idx = search_opcode(opr, inst_table, inst_table_length);
    if (inst_idx >= 0 && inst_table[inst_idx]->format != 0 && tok->operand[0] != NULL) {
        char c = tok->operand[0][0];
        if (c == '@') tok->nixbpe += (1 << 5);      // 10 0000
        else if (c == '#') tok->nixbpe += (1 << 4); // 01 0000
        else tok->nixbpe += (3 << 4);               // 11 0000

        if (opnd_cnt > 0 && strcmp(tok->operand[opnd_cnt - 1], "X") == 0) 
            tok->nixbpe += (1 << 3); // 00 1000
    }
    if (tok->operator != NULL && tok->operator[0] == '+')
        tok->nixbpe += 1;
    return 0;
}

/**
 * @brief 기계어 목록 테이블에서 특정 기계어를 검색하여, 해당 기계에가 위치한
 * 인덱스를 반환한다.
 *
 * @param str 검색할 기계어 문자열
 * @param inst_table 기계어 목록 테이블 주소
 * @param inst_table_length 기계어 목록 테이블의 길이
 * @return 기계어의 인덱스 (해당 기계어가 없는 경우 -1)
 *
 * @details
 * 기계어 목록 테이블에서 특정 기계어를 검색하여, 해당 기계에가 위치한 인덱스를
 * 반환한다. '+JSUB'와 같은 문자열에 대한 처리는 자유롭게 처리한다.
 */
int search_opcode(const char *str, const inst *inst_table[],
                  int inst_table_length) {
    if (str[0] == '+') return search_opcode(str + 1, inst_table, inst_table_length);

    for (int i = 0; i < inst_table_length; i++) {
        if (strcmp(str, inst_table[i]->str) == 0)
            return i;
    }
    return -1;
}

/**
 * @brief 소스코드 명령어 앞에 OPCODE가 기록된 코드를 파일에 출력한다.
 * `output_dir`이 NULL인 경우 결과를 stdout으로 출력한다. 프로젝트 1에서는
 * 불필요하다.
 *
 * @param output_dir 코드를 저장할 파일 경로, 혹은 NULL
 * @param tokens 토큰 테이블 주소
 * @param tokens_length 토큰 테이블의 길이
 * @param inst_table 기계어 목록 테이블 주소
 * @param inst_table_length 기계어 목록 테이블의 길이
 * @return 오류 코드 (정상 종료 = 0)
 *
 * @details
 * 소스코드 명령어 앞에 OPCODE가 기록된 코드를 파일에 출력한다. `output_dir`이
 * NULL인 경우 결과를 stdout으로 출력한다. 명세서에 주어진 출력 예시와 완전히
 * 동일할 필요는 없다. 프로젝트 1에서는 불필요하다.
 */
int make_opcode_output(const char *output_dir, const token *tokens[],
                       int tokens_length, const inst *inst_table[],
                       int inst_table_length) {
    /* add your code */

    return 0;
}

/**
 * @brief 어셈블리 코드을 위한 패스 2 과정을 수행한다.
 *
 * @param tokens 토큰 테이블 주소
 * @param tokens_length 토큰 테이블 길이
 * @param inst_table 기계어 목록 테이블 주소
 * @param inst_table_length 기계어 목록 테이블 길이
 * @param symbol_table 심볼 테이블 주소
 * @param symbol_table_length 심볼 테이블 길이
 * @param literal_table 리터럴 테이블 주소
 * @param literal_table_length 리터럴 테이블 길이
 * @param obj_code 오브젝트 코드에 대한 정보를 저장하는 구조체 주소
 * @return 오류 코드 (정상 종료 = 0)
 *
 * @details
 * 어셈블리 코드를 기계어 코드로 바꾸기 위한 패스2 과정을 수행한다. 패스 2의
 * 프로그램을 기계어로 바꾸는 작업은 라인 단위로 수행된다.
 */
int assem_pass2(const token *tokens[], int tokens_length,
                const inst *inst_table[], int inst_table_length,
                const symbol *symbol_table[], int symbol_table_length,
                const literal *literal_table[], int literal_table_length,
                object_code *obj_code) {
    /* add your code */

    return 0;
}

/**
 * @brief 심볼 테이블을 파일로 출력한다. `symbol_table_dir`이 NULL인 경우 결과를
 * stdout으로 출력한다.
 *
 * @param symbol_table_dir 심볼 테이블을 저장할 파일 경로, 혹은 NULL
 * @param symbol_table 심볼 테이블 주소
 * @param symbol_table_length 심볼 테이블 길이
 * @return 오류 코드 (정상 종료 = 0)
 *
 * @details
 * 심볼 테이블을 파일로 출력한다. `symbol_table_dir`이 NULL인 경우 결과를
 * stdout으로 출력한다. 명세서에 주어진 출력 예시와 완전히 동일할 필요는 없다.
 */
int make_symbol_table_output(const char *symbol_table_dir,
                             const symbol *symbol_table[],
                             int symbol_table_length) {
    /* add your code */

    return 0;
}

/**
 * @brief 리터럴 테이블을 파일로 출력한다. `literal_table_dir`이 NULL인 경우
 * 결과를 stdout으로 출력한다.
 *
 * @param literal_table_dir 리터럴 테이블을 저장할 파일 경로, 혹은 NULL
 * @param literal_table 리터럴 테이블 주소
 * @param literal_table_length 리터럴 테이블 길이
 * @return 오류 코드 (정상 종료 = 0)
 *
 * @details
 * 리터럴 테이블을 파일로 출력한다. `literal_table_dir`이 NULL인 경우 결과를
 * stdout으로 출력한다. 명세서에 주어진 출력 예시와 완전히 동일할 필요는 없다.
 */
int make_literal_table_output(const char *literal_table_dir,
                              const literal *literal_table[],
                              int literal_table_length) {
    /* add your code */

    return 0;
}

/**
 * @brief 오브젝트 코드를 파일로 출력한다. `objectcode_dir`이 NULL인 경우 결과를
 * stdout으로 출력한다.
 *
 * @param objectcode_dir 오브젝트 코드를 저장할 파일 경로, 혹은 NULL
 * @param obj_code 오브젝트 코드에 대한 정보를 담고 있는 구조체 주소
 * @return 오류 코드 (정상 종료 = 0)
 *
 * @details
 * 오브젝트 코드를 파일로 출력한다. `objectcode_dir`이 NULL인 경우 결과를
 * stdout으로 출력한다. 명세서의 주어진 출력 결과와 완전히 동일해야 한다.
 * 예외적으로 각 라인 뒤쪽의 공백 문자 혹은 개행 문자의 차이는 허용한다.
 */
int make_objectcode_output(const char *objectcode_dir,
                           const object_code *obj_code) {
    /* add your code */

    return 0;
}