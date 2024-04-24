/**
 * @author Enoch Jung (github.com/enochjung)
 * @file my_assembler_parsing.c
 * @date 2024-04-15
 * @version 0.1.0
 *
 * @brief 조교가 구현한 SIC/XE 코드 파싱 예시
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* 파일명의 "00000000"은 자신의 학번으로 변경할 것 */
#include "my_assembler_parsing_20222904.h"

#define MAX_COMMENT_SIZE 100

#define ERR_FILE_IO_FAIL -100
#define ERR_ALLOCATION_FAIL -200
#define ERR_ARRAY_OVERFLOW -1000
#define ERR_ILLEGAL_OPERATOR -10200
#define ERR_ILLEGAL_OPERAND_FORMAT -10300

static int add_inst_to_table(inst *inst_table[], int *inst_table_length,
                             const char *buffer);
static int token_operand_parsing(const char *operand_input,
                                 int operand_input_length, char *operand[]);
static int write_opcode_output(FILE *fp, const token *tok,
                               const inst *inst_table[], int inst_table_length);

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
    /** 파싱 과제에서는 불필요함 */
    // object_code *obj_code = (object_code *)malloc(sizeof(object_code));

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

    if ((err = make_opcode_output(NULL, (const token **)tokens, tokens_length,
                                  (const inst **)inst_table,
                                  inst_table_length)) < 0) {
        fprintf(stderr,
                "make_opcode_output: opcode 파일 출력 과정에서 실패했습니다. "
                "(error_code: %d)\n",
                err);
        return -1;
    }

    /** 파싱 과제에서는 불필요함 */
    /*
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
    */

    /** 메모리 해제따위 */

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
    /** 전 귀찮아서 지시어도 inst_table.txt에 때려박았습니다 */
    FILE *fp;
    int err;

    char buffer[20];

    if ((fp = fopen(inst_table_dir, "r")) == NULL) return ERR_FILE_IO_FAIL;

    while (!feof(fp)) {
        fgets(buffer, 20, fp);
        err = add_inst_to_table(inst_table, inst_table_length, buffer);
        if (err != 0) {
            fclose(fp);
            return err;
        }
    }

    if (fclose(fp) != 0) return ERR_FILE_IO_FAIL;
    err = 0;

    return 0;
}

/**
 * @brief inst_table.txt의 라인 하나를 입력으로 받아, 해당하는 instruction
 * 정보를 inst_table에 저장함.
 */
static int add_inst_to_table(inst *inst_table[], int *inst_table_length,
                             const char *buffer) {
    char name[10];
    char ops[10];
    int format;
    char op[10];

    if (*inst_table_length == MAX_INST_TABLE_LENGTH) return ERR_ARRAY_OVERFLOW;

    sscanf(buffer, "%s %s %d %s\n", name, ops, &format, op);

    if ((inst_table[*inst_table_length] = (inst *)malloc(sizeof(inst))) == NULL)
        return ERR_ALLOCATION_FAIL;

    memcpy(inst_table[*inst_table_length]->str, name, 9);
    inst_table[*inst_table_length]->str[9] = '\0';

    /** !제 마음대로 저장했습니다. 여기선 ops가 operand 개수가 아닙니다! */
    if (ops[0] == '-')
        inst_table[*inst_table_length]->ops = 0;
    else if (ops[0] == 'M')
        inst_table[*inst_table_length]->ops = 1;
    else if (ops[0] == 'N')
        inst_table[*inst_table_length]->ops = 3;
    else if (ops[1] == 'R')
        inst_table[*inst_table_length]->ops = 4;
    else if (ops[1] == 'N')
        inst_table[*inst_table_length]->ops = 5;
    else
        inst_table[*inst_table_length]->ops = 2;

    inst_table[*inst_table_length]->format = format;

    inst_table[*inst_table_length]->op = (char)strtol(op, NULL, 16);

    ++(*inst_table_length);

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
    FILE *fp;

    char buffer[250];
    int length;

    if ((fp = fopen(input_dir, "r")) == NULL) return ERR_FILE_IO_FAIL;

    while (!feof(fp)) {
        if (fgets(buffer, 249, fp) == NULL) break;
        buffer[249] = '\0';
        length = (int)strlen(buffer);
        if ((input[*input_length] = (char *)malloc(length + 1)) == NULL) {
            fclose(fp);
            return ERR_ALLOCATION_FAIL;
        }
        sscanf(buffer, "%[^\r^\n]", input[*input_length]);

        ++(*input_length);
    }

    if (fclose(fp) != 0) return ERR_FILE_IO_FAIL;

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
                const char *input[], int input_length, token *tokens[],
                int *tokens_length, symbol *symbol_table[],
                int *symbol_table_length, literal *literal_table[],
                int *literal_table_length) {
    /** 파싱 과제에서는 symbol_table 및 literal_table 세팅을 수행하지 않음 */
    int err;

    for (int i = 0; i < input_length; ++i) {
        if ((tokens[i] = (token *)malloc(sizeof(token))) == NULL)
            return ERR_ALLOCATION_FAIL;
        if ((err = token_parsing(input[i], tokens[i], inst_table,
                                 inst_table_length)) != 0)
            return err;
    }

    *tokens_length = input_length;

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
#define PARSE_WITHOUT_SCANF
int token_parsing(const char *input, token *tok, const inst *inst_table[],
                  int inst_table_length) {
    int input_length = strlen(input);

    tok->label = NULL;
    tok->operator= NULL;
    tok->operand[0] = NULL;
    tok->operand[1] = NULL;
    tok->operand[2] = NULL;
    tok->comment = NULL;

    if (input[0] == '.') {
        if ((tok->comment = (char *)malloc(input_length)) == NULL)
            return ERR_ALLOCATION_FAIL;
        sscanf(input + 1, " %[^\0]", tok->comment);
        tok->comment[input_length] = '\0';
    } else {
#ifdef PARSE_WITHOUT_SCANF
        /** 이렇게 루프 돌면서 직접 파싱하는 걸 권장합니다 */
        int token_cnt = 0;
        for (int st = 0; st < input_length && token_cnt < 3; ++st) {
            int end = st;
            for (; input[end] != '\t' && input[end] != '\0'; ++end)
                ;

            switch (token_cnt) {
                case 0:
                    if (st < end) {
                        if ((tok->label = (char *)malloc(end - st + 1)) == NULL)
                            return ERR_ALLOCATION_FAIL;
                        memcpy(tok->label, input + st, end - st);
                        tok->label[end - st] = '\0';
                    }
                    break;

                case 1:
                    if (st < end) {
                        if ((tok->operator=(char *) malloc(end - st + 1)) ==
                            NULL)
                            return ERR_ALLOCATION_FAIL;
                        memcpy(tok->operator, input + st, end - st);
                        tok->operator[end - st] = '\0';
                    }
                    break;

                case 2:
                    if (st < end) {
                        int err;
                        if ((err = token_operand_parsing(input + st, end - st,
                                                         tok->operand)) != 0)
                            return err;
                    }

                    st = end + 1;
                    end = input_length;
                    if (st < end) {
                        if ((tok->comment = (char *)malloc(end - st + 1)) ==
                            NULL)
                            return ERR_ALLOCATION_FAIL;
                        memcpy(tok->comment, input + st, end - st);
                        tok->comment[end - st] = '\0';
                    }
            }

            ++token_cnt;
            st = end;
        }
#else
        /** 전 귀찮아서 scanf로 했었습니다 */
        char label[100] = {};
        char opr[100] = {};
        char opd[100] = {};
        char comment[100] = {};

        const char *str = input;

        int n = 0;
        sscanf(str, "%[^\t]%n", label, &n);
        str += (str[n] != '\0' ? n + 1 : n);
        n = 0;
        sscanf(str, "%[^\t]%n", opr, &n);
        str += (str[n] != '\0' ? n + 1 : n);
        n = 0;
        sscanf(str, "%[^\t]%n", opd, &n);
        str += (str[n] != '\0' ? n + 1 : n);
        sscanf(str, "%[^\0]", comment);

        if (label[0] != '\0') {
            int len = strlen(label);
            tok->label = (char *)malloc(len + 1);
            memcpy(tok->label, label, len + 1);
        }
        if (opr[0] != '\0') {
            int len = strlen(opr);
            tok->operator=(char *) malloc(len + 1);
            memcpy(tok->operator, opr, len + 1);
        }
        if (opd[0] != '\0') {
            int len = strlen(opd);
            token_operand_parsing(opd, len, tok->operand);
        }
        if (comment[0] != '\0') {
            int len = strlen(comment);
            tok->comment = (char *)malloc(len + 1);
            memcpy(tok->comment, comment, len + 1);
        }
#endif
    }

    return 0;
}

/**
 * @brief 피연산자 문자열을 파싱하여 operand에 저장함. 문자열은 \0으로 끝나지
 * 않을 수 있기에, operand_input_length로 문자열의 길이를 전달해야 함.
 */
static int token_operand_parsing(const char *operand_input,
                                 int operand_input_length, char *operand[]) {
#ifdef PARSE_WITHOUT_SCANF
    /** 직접 구현해보니 많이 귀찮네요 */
    int operand_cnt = 0;
    for (int st = 0; st < operand_input_length; ++st) {
        int end = st;

        for (; operand_input[end] != ',' && operand_input[end] != '\t' &&
               operand_input[end] != '\0';
             ++end)
            ;

        switch (operand_cnt) {
            case 0:
                if ((operand[0] = (char *)malloc(end - st + 1)) == NULL)
                    return ERR_ALLOCATION_FAIL;
                memcpy(operand[0], operand_input + st, end - st);
                operand[0][end - st] = '\0';
                break;

            case 1:
                if ((operand[1] = (char *)malloc(end - st + 1)) == NULL)
                    return ERR_ALLOCATION_FAIL;
                memcpy(operand[1], operand_input + st, end - st);
                operand[1][end - st] = '\0';
                break;

            case 2:
                if ((operand[2] = (char *)malloc(end - st + 1)) == NULL)
                    return ERR_ALLOCATION_FAIL;
                memcpy(operand[2], operand_input + st, end - st);
                operand[2][end - st] = '\0';
                if (end != operand_input_length)
                    return ERR_ILLEGAL_OPERAND_FORMAT;
                break;
        }

        ++operand_cnt;
        st = end;
    }
#else
    /** 대충 파싱 */
    char o0[100] = {};
    char o1[100] = {};
    char o2[100] = {};

    const char *str = operand_input;

    int n = 0;
    sscanf(str, "%[^,]%n", o0, &n);
    str += (str[n] != '\0' ? n + 1 : n);
    n = 0;
    sscanf(str, "%[^,]%n", o1, &n);
    str += (str[n] != '\0' ? n + 1 : n);
    sscanf(str, "%[^\0]", o2);

    if (o0[0] != '\0') {
        int len = strlen(o0);
        operand[0] = (char *)malloc(len + 1);
        memcpy(operand[0], o0, len + 1);
    }
    if (o1[0] != '\0') {
        int len = strlen(o1);
        operand[1] = (char *)malloc(len + 1);
        memcpy(operand[1], o1, len + 1);
    }
    if (o2[0] != '\0') {
        int len = strlen(o2);
        operand[2] = (char *)malloc(len + 1);
        memcpy(operand[2], o2, len + 1);
    }
#endif

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
    /** 함수명을 search_instruction으로 정했어야 했는데... 강을 많이 건넜네요 */
    if (str[0] == '+')
        return search_opcode(str + 1, inst_table, inst_table_length);

    for (int i = 0; i < inst_table_length; ++i) {
        if (strcmp(str, inst_table[i]->str) == 0) return i;
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
    FILE *fp;
    int err = 0;

    if (output_dir == NULL)
        fp = stdout;
    else if ((fp = fopen(output_dir, "w")) == NULL)
        return ERR_FILE_IO_FAIL;

    for (int i = 0; i < tokens_length; ++i) {
        if ((err = write_opcode_output(fp, tokens[i], inst_table,
                                       inst_table_length)) != 0) {
            break;
        }
    }

    if (fp != stdout) {
        if (fclose(fp) != 0) return ERR_FILE_IO_FAIL;
    }

    return 0;
}

/**
 * @brief 토큰 하나에 담긴 정보를 fp에 출력함.
 */
static int write_opcode_output(FILE *fp, const token *tok,
                               const inst *inst_table[],
                               int inst_table_length) {
    if (tok->label == NULL && tok->operator== NULL) {
        fprintf(fp, ".\t%s\n", tok->comment == NULL ? "" : tok->comment);
        return 0;
    }

    fprintf(fp, "%-8s%-8s", (tok->label != NULL?tok->label : ""), (tok->operator != NULL?tok->operator:""));

    char buffer[50] = {};
    if (tok->operand[2] != NULL)
        sprintf(buffer, "%s,%s,%s", tok->operand[0], tok->operand[1],
                tok->operand[2]);
    else if (tok->operand[1] != NULL)
        sprintf(buffer, "%s,%s", tok->operand[0], tok->operand[1]);
    else if (tok->operand[0] != NULL)
        sprintf(buffer, "%s", tok->operand[0]);

    fprintf(fp, "%-26s", buffer);

    if (tok->operator!= NULL) {
        int inst_idx =
            search_opcode(tok->operator, inst_table, inst_table_length);
        if (inst_idx < 0) return ERR_ILLEGAL_OPERATOR;

        if (inst_table[inst_idx]->format > 0) {
            unsigned char op = inst_table[inst_idx]->op;
            fprintf(fp, "%02X", op);
        } else
            fprintf(fp, "  ");
    }

    fprintf(fp, "    %s\n", tok->comment != NULL ? tok->comment : "");

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
 * 예외적으로 각 라인의 뒤쪽 공백 문자 혹은 개행 문자의 차이는 허용한다.
 */
int make_objectcode_output(const char *objectcode_dir,
                           const object_code *obj_code) {
    /* add your code */

    return 0;
}