package syntaxAnalysis;

import java.util.ArrayList;

import lexicalAnalysis.Token;
import lexicalAnalysis.TokenType;
import error.*;

// Author��Andersen
// �﷨����������������Լ��м�����������
// �Ƚϸ���

public class SyntaxAnalysis {

	private ArrayList<Token> tokenList;
	private int index = 0; // ��һ��Ҫȡ��Token���
	private Token token; // ȫ��ʹ�õ�token

	// ������
	private FuncTable funcTable = new FuncTable();
	// ������������
	private TokenType resType;
	// ������������
	private int paraNum;
	// ������Ϣ��
	private Func func = null;

	// ����
	private Block block = null;
	// ������
	private int NO = 1;
	// ������
	private Table table = new Table();

	// ջƫ��
	private int offset;

	// �������ṩ�Ĳ�������
	private int callParaNum;

	// ָ���ı�
	private Text text;

	public SyntaxAnalysis(ArrayList<Token> tokenList) {
		this.tokenList = tokenList;
	}

	private Token getToken() {
		if (index == tokenList.size()) {
			return null;
		} else {
			return tokenList.get(index++);
		}
	}

	private void reToken() {
		index--;
	}

	// ���еݹ��½��ӳ��򷵻�ֵ��Ϊint����
	// ����
	// 1��ʾ��ǰ�������
	// 0��ʾ���ڶ���ĩβ���޷���ͷ��ʼƥ��
	// -1��ʾ����ͷ���ż���ƥ������µ��޷�ƥ��
	// -2��ʾ�����﷨��������󣬵�ʵ����Ӧ���޷��õ��÷���ֵ
	// ;�в����﷨��������������ʱ��ֱ�ӵ���Err.error()���б���ֱ���˳�����

	// �﷨���������
	public FuncTable syntaxAnalysis() {
		if (analyseC0Program() == 1) {
			// ����Ƿ���main����
			for (Func func : funcTable.getFuncList()) {
				if (func.name.contentEquals("main")) {
					System.out.println("success");
					return funcTable;
				}
			}
			Err.error(ErrEnum.NO_MAIN_ERR);
			return null;
		} else {
			System.out.println("fail");
			return null;
		}
	}

	// ���ڳ������������ͺ���������ͷ���ż��н��������Զ���һ�����ⷽ��
	// ֻ�����ж��������������ж���ɺ�reToken()����Ӱ���������
	// ֻ����ͷ���ż��������ĺ����Ƿ���ƥ����
	// ����0��ʾ�Ѿ�����EOF
	// ����1��ʾ�ǳ�����������
	// ����2��ʾ�Ǻ�������
	// ���෵�ؾ������쳣
	private int analyseVarOrFunc() {
		token = getToken();
		// ��ʼ�Ͷ���EOF������0
		if (token == null) {
			return 0;
		}
		// ����CONST���϶��ǳ������������������˵�ǳ���������
		if (token.getType() == TokenType.CONST) {
			reToken(); // ���ˣ���Ӱ���������
			return 1;
		}
		// ����VOID���϶��Ǻ�������
		if (token.getType() == TokenType.VOID) {
			reToken(); // ���ˣ���Ӱ���������
			return 2;
		}
		// ����INT
		// int a;
		// int a, ...
		// int a = ...
		// int a( ...
		if (token.getType() == TokenType.INT) {
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR); // ������EOF��������
				return -2;
			}
			// INT��϶���һ��ID�����򱨴�
			if (token.getType() != TokenType.ID) {
				Err.error(ErrEnum.NEED_ID_ERR);
				return -2;
			}
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR); // ������EOF��������
				return -2;
			}
			// ֻ��LSB��ƥ�亯������������Ķ�ƥ�䵽�������������ں����Ƿ����ƥ�䲢������
			if (token.getType() == TokenType.LSB) {
				// ����Ϊֹ��������token����������
				reToken();
				reToken();
				reToken();
				return 2;
			}
			// ����Ϊֹ��������token����������
			reToken();
			reToken();
			reToken();
			return 1;
		}
		// ����ߵ�ͷ���ż�����ƥ�䣬����
		Err.error(ErrEnum.US_TYPE_ERR);
		return -2;
	}

	// <C0-program> ::= {<variable-declaration>}{<function-definition>}
	private int analyseC0Program() {
		// ��¼ȫ�ֵı��������Ƿ��Ѿ�������
		boolean flag = false;
		// {<variable-declaration>}
		// Ϊȫ�ֳ��������½�һ��Block�����Ϊ0�������Ϊ-1
		block = new Block(0, -1);
		table.addBlock(block);
		// Ϊȫ�ֳ��������ĳ�ʼ�������½������
		text = new Text("ȫ��"); // �����ĵ����֣���ֹ�ͺ���ĺ�������
		while (true) {
			int res = analyseVarOrFunc();
			// ������������
			if (res == 1) {
				// �Ƿ�����ֵֻ��-2
				if (analyseVariableDeclaration(0) == -2) {
					Err.error(ErrEnum.SP_ERR);
					return -2;
				}
			}
			// ��������
			else if (res == 2) {
				// �ոս�����ȫ�ֱ�������
				if (flag == false) {
					// Ϊȫ�ֱ�����������һ����������ȫ�֡�
					func = new Func("ȫ��", null, null);
					func.addText(text);
					funcTable.addFunc(func);
					flag = true;
				}
				// �Ƿ�����ֵΪ-1��-2
				int temp = analyseFunctionDefinition();
				if (temp == -1 || temp == -2) {
					Err.error(ErrEnum.SP_ERR);
					return -2;
				}
			}
			// ģ���龰�������һ���������������
			// �ٴε���analyseVarOrFunc()������EOF������0
			else if (res == 0) {
				return 1;
			}
			// ������������õ����õĴ�����
			else {
				Err.error(ErrEnum.SP_ERR);
				return -2;
			}
		}
	}

	// <variable-declaration> ::=
	// [<const-qualifier>]<type-specifier><init-declarator-list>';'
	private int analyseVariableDeclaration(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		// ��������
		if (token.getType() == TokenType.CONST) {
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			// ����C0�н���������������ֻ����INT
			if (token.getType() == TokenType.INT) {
				// <init-declarator-list>���뱻��ʾ��ʼ��
				// Ϊ����������ӵ����ⷽ�� analyseInitDeclaratorListForConst()
				if (analyseInitDeclaratorListForConst(no) == 1) {

					token = getToken();
					if (token == null) {
						Err.error(ErrEnum.EOF_ERR);
						return -2;
					}
					if (token.getType() != TokenType.SEM) {
						Err.error(ErrEnum.SEM_ERR);
						return -2;
					}
					return 1;
				}
				Err.error(ErrEnum.CONST_DECL_ERR);
				return -2;
			}
			// ��֧�ֵı�������
			else {
				Err.error(ErrEnum.US_TYPE_ERR);
				return -2;
			}
		}
		// ��������
		else if (token.getType() == TokenType.INT) {
			// <init-declarator-list>
			if (analyseInitDeclaratorList(no) == 1) {
				token = getToken();
				if (token == null) {
					Err.error(ErrEnum.EOF_ERR);
					return -2;
				}
				if (token.getType() != TokenType.SEM) {
					Err.error(ErrEnum.SEM_ERR);
					return -2;
				}
				return 1;
			}
			Err.error(ErrEnum.VAR_DECL_ERR);
			return -2;
		}
		// ͷ���ż���ƥ�䣬���ǳ�����������
		reToken();
		return -1;
	}

	// <init-declarator-list> ::= <init-declarator>{','<init-declarator>}
	// Ϊ����������ӵķ�����Ҫ��<init-declarator>�б�����ʽ��ʼ��
	private int analyseInitDeclaratorListForConst(int no) {
		// �����ĳ���������һ��
		if (analyseInitDeclaratorForConst(no) == 1) {
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				if (token.getType() != TokenType.COMMA) {
					reToken();
					return 1;
				}
				if (analyseInitDeclaratorForConst(no) != 1) {
					Err.error(ErrEnum.CONST_DECL_ERR);
					return -2;
				}
			}
		}
		// û�г������������﷨����
		Err.error(ErrEnum.CONST_DECL_ERR);
		return -2;
	}

	// <init-declarator> ::= <identifier><initializer>
	// Ϊ����������ӵķ������������﷨��Ҫ�������<initializer>
	// ����ֱ�ӽ�<initializer>ת��Ϊ'='<expression>
	// �����﷨��<init-declarator> ::= <identifier>'='<expression>
	private int analyseInitDeclaratorForConst(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.ID) {
			String name = token.getValue();
			// �����Ƿ����ض���
			// ע�⣬�ض���ֻ�ڱ����в��ң������ϵݹ�
			// ͬʱ�����뺯��������
			if (block.containsID(name)) {
				Err.error(ErrEnum.ID_REDECL_ERR);
				return -2;
			}
			token = getToken();
			// û����ʽ��ʼ��
			if (token == null) {
				// ���ﻹ�Ƿ���EOF_ERR
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.E) {
				Err.error(ErrEnum.CONST_INIT_ERR);
				return -2;
			}
			// ��ʽ��ʼ��
			if (analyseExpression(no) != 1) {
				Err.error(ErrEnum.EXP_ERR);
				return -2;
			}
			// ���ű��ϼ�¼ջƫ��
			// �˴������ض��壬����δ��ʼ��
			block.put(0, name, offset++);
			return 1;
		}
		// ͷ���ż���ƥ��
		reToken();
		return -1;
	}

	// <init-declarator-list> ::= <init-declarator>{','<init-declarator>}
	private int analyseInitDeclaratorList(int no) {
		// ������һ������������
		if (analyseInitDeclarator(no) == 1) {
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				if (token.getType() != TokenType.COMMA) {
					reToken();
					return 1;
				}
				if (analyseInitDeclarator(no) != 1) {
					Err.error(ErrEnum.VAR_DECL_ERR);
					return -2;
				}
			}
		}
		// û�б���������
		Err.error(ErrEnum.VAR_DECL_ERR);
		return -2;
	}

	// <init-declarator> ::= <identifier>[<initializer>]
	// ����ֱ�ӽ�<initializer>ת��Ϊ'='<expression>
	private int analyseInitDeclarator(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.ID) {
			String name = token.getValue();
			// �����Ƿ����ض���
			// ͬ����ֻ�ڱ����в���
			if (block.containsID(name)) {
				Err.error(ErrEnum.ID_REDECL_ERR);
				return -2;
			}
			token = getToken();
			// û����ʽ��ʼ��
			if (token == null) {
				// δ��ʼ��ռλ��
				text.addCode("ipush", "0", "");
				block.put(-1, name, offset++);
				return 1;
			}
			if (token.getType() != TokenType.E) {
				reToken();
				// δ��ʼ��ռλ��
				text.addCode("ipush", "0", "");
				block.put(-1, name, offset++);
				return 1;
			}
			// ��ʽ��ʼ��
			if (analyseExpression(no) != 1) {
				Err.error(ErrEnum.EXP_ERR);
				return -2;
			}
			// ���ű��ϼ�¼ջƫ��
			block.put(1, name, offset++);
			return 1;
		}
		// ͷ���ż���ƥ��
		reToken();
		return -1;
	}

	// <expression> ::= <add-expression>
	private int analyseExpression(int no) {
		if (analyseAddExpression(no) != 1) {
			Err.error(ErrEnum.EXP_ERR);
			return -2;
		}
		return 1;
	}

	// <add-expression> ::= <mul-expression>{<add-operator><mul-expression>}
	// �ӷ��ͱ��ʽ���� '+' / '-' �� ...
	private int analyseAddExpression(int no) {
		if (analyseMulExpression(no) == 1) {
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				// ��type����¼operator������
				TokenType type = token.getType();
				if (type != TokenType.PLUS && type != TokenType.MINUS) {
					// ���ǼӼ���
					reToken();
					return 1;
				}
				if (analyseMulExpression(no) != 1) {
					Err.error(ErrEnum.EXP_ERR);
					return -2;
				}
				// ��ǰ������ɹ�
				if (type == TokenType.PLUS) {
					text.addCode("iadd", "", "");
				} else if (type == TokenType.MINUS) {
					text.addCode("isub", "", "");
				}
			}
		}
		Err.error(ErrEnum.EXP_ERR);
		return -2;
	}

	// <mul-expression> ::= <cast-expression>{<mul-operator><cast-expression>}
	// �˷��ͱ��ʽ������ '*' / '/' ����
	private int analyseMulExpression(int no) {
		if (analyseCastExpression(no) == 1) {
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				TokenType type = token.getType();
				if (type != TokenType.MUL && type != TokenType.DIV) {
					// ����
					reToken();
					return 1;
				}
				if (analyseCastExpression(no) != 1) {
					Err.error(ErrEnum.EXP_ERR);
					return -2;
				}
				// ��ǰ���ӷ����ɹ�
				if (type == TokenType.MUL) {
					text.addCode("imul", "", "");
				} else if (type == TokenType.DIV) {
					text.addCode("idiv", "", "");
				}
			}
		}
		Err.error(ErrEnum.EXP_ERR);
		return -2;
	}

	// <cast-expression> ::= {'('<type-specifier>')'}<unary-expression>
	// ���ڻ���C0�в�����������ת�������������﷨
	// <cast-expression> ::= <unary-expression>
	private int analyseCastExpression(int no) {
		if (analyseUnaryExpression(no) != 1) {
			Err.error(ErrEnum.EXP_ERR);
			return -2;
		}
		return 1;
	}

	// <unary-expression> ::= [<unary-operator>]<primary-expression>
	private int analyseUnaryExpression(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		@SuppressWarnings("unused") // ��ʱ���ϣ���ֹwarning
		int prefix = 1; // ����λ��û�з���λĬ��Ϊ������1
		// ����λ������type��
		TokenType type = token.getType();
		if (type == TokenType.PLUS || type == TokenType.MINUS) {
			if (type == TokenType.PLUS) {
				prefix = 1;
			} else if (type == TokenType.MINUS) {
				prefix = -1;
				text.addCode("ipush", "0", "");
			}
			// ����<primary-expression>
			if (analysePrimaryExpression(no) != 1) {
				Err.error(ErrEnum.EXP_ERR);
				return -2;
			}
			if (type == TokenType.MINUS) {
				text.addCode("isub", "", "");
			}
			return 1;
		}
		// û�з���λ
		else {
			reToken();
			// ����<primary-expression>
			if (analysePrimaryExpression(no) == 1) {
				return 1;
			}
			// ������ϸ���Ǻ���ͷ���ż���ƥ������
			else {
				// ���ﲻ��reToken()
				return -1;
			}
		}

	}

	// <primary-expression> ::=
	// '('<expression>')'
	// |<identifier>
	// |<integer-literal>
	// |<char-literal>
	// |<floating-literal>
	// |<function-call>
	private int analysePrimaryExpression(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		// '('<expression>')'
		if (token.getType() == TokenType.LSB) {
			if (analyseExpression(no) == 1) {
				token = getToken();
				if (token == null) {
					Err.error(ErrEnum.EOF_ERR);
					return -2;
				}
				// ȱ����-С����
				if (token.getType() != TokenType.RSB) {
					Err.error(ErrEnum.RSB_ERR);
					return -2;
				}
				return 1;
			}
			Err.error(ErrEnum.EXP_ERR);
			return -2;
		}
		// <identifier>
		else if (token.getType() == TokenType.ID) {
			String name = token.getValue();
			Integer res = table.getKind(name, no);
			// ������
			if (res == null) {
				Err.error(ErrEnum.ID_UNDECL_ERR);
				return -2;
			}
			// δ��ʼ��
			if (res == -1) {
				Err.error(ErrEnum.VAR_UNINIT_ERR);
				return -2;
			}
			// ���ջƫ��
			Offset off = table.getOffset(name, no);

			// level = 1
			if (off.no == 0 && no != 0) {
				text.addCode("loada", "1", off.offset.toString());
			}
			// level = 0
			else {
				text.addCode("loada", "0", off.offset.toString());
			}
			text.addCode("iload", "", "");
			return 1;
		}
		// <integer-literal>
		else if (token.getType() == TokenType.DEC_INT) {
			text.addCode("ipush", token.getValue(), "");
			return 1;
		}
		// ��������
		int res = analyseFunctionCall(no);
		// ���ʽ��ʹ�÷���ֵΪ�յĺ�������
		if (res == 1) {
			Err.error(ErrEnum.VOID_FUNC_CALL_ERR);
			return -2;
		} else if (res == 2) {
			return 1;
		}
		// ͷ���ż���ƥ��
		else {
			reToken();
			return -1;
		}
	}

	// <function-definition> ::=
	// <type-specifier><identifier><parameter-clause><compound-statement>
	private int analyseFunctionDefinition() {
		// ����C0�У�<function-definition>��FIRST��ΪINT��VOID
		token = getToken();
		// �����β
		if (token == null) {
			return 0;
		}
		// ��¼������������
		resType = token.getType();
		if (resType == TokenType.INT || resType == TokenType.VOID) {
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() == TokenType.ID) {
				String name = token.getValue();
				// �к�������
				if (funcTable.containsFunc(name)) {
					Err.error(ErrEnum.FUNC_REDECL_ERR);
					return -2;
				}

				// ���ｫ��������������������һ����
				// ע�⣬�������Ͳ���������������һ�������ŵ�������
				// �����ĸ���һ����ȫ�ֿ�0
				// ջƫ������
				offset = 0;
				// ������������
				paraNum = 0;
				int no = NO++;
				// �����޴��Ĵ��󣬵���Block���ݽṹ����û�б�Ҫ
				// ����ÿ������ֻ����һ��Block
				block = new Block(no, 0);
				table.addBlock(block);
				// �½������
				text = new Text(name);
				// ��ʼ���������б�
				// ע�⣬����Ҳ�����ֲ���������
				if (analyseParameterClause() == 1) {
					// �����������
					func = new Func(name, resType, paraNum);
					// text���ϵ�func��Ӧλ��
					func.addText(text);
					funcTable.addFunc(func);
					if (analyseCompoundStatement(no) == 1) {
						// ����block
						// ��ʵû��Ҫ
						block = table.getBlock(block.fatherNo);
						// ������Σ�������Ҫ�������
						if (resType == TokenType.VOID) {
							text.addCode("ret", "", "");
						} else {
							text.addCode("ipush", "0", "");
							text.addCode("iret", "", "");
						}
						return 1;
					}
					// ��������﷨����
					else {
						Err.error(ErrEnum.FUNC_STATMENT_ERR);
						return -2;
					}
				}
				// ������������ʱ�﷨����
				else {
					Err.error(ErrEnum.FUNC_PARA_DECL_ERR);
					return -2;
				}
			}
			// ȱ�ٱ�ʶ��
			else {
				Err.error(ErrEnum.NEED_ID_ERR);
				return -2;
			}
		}
		reToken();
		return -1;
	}

	// <parameter-clause> ::= '(' [<parameter-declaration-list>] ')'
	private int analyseParameterClause() {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.LSB) {
			// ������Ԥ��һ��token�ж��Ƿ��в���
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			// ֱ������-С���ţ�����û�в���
			if (token.getType() == TokenType.RSB) {
				// ֱ��ƥ����<parameter-clause> ::= '(' ')'
				// ���Բ����б�����κ��޸�
				return 1;
			}
			// ������-С���ţ������в���
			else {
				reToken(); // �Ȼ���
				// ����Ϸ�����ֵֻ��1
				if (analyseParameterDeclarationList() == 1) {
					token = getToken();
					if (token == null) {
						Err.error(ErrEnum.EOF_ERR);
						return -2;
					}
					if (token.getType() != TokenType.RSB) {
						Err.error(ErrEnum.RSB_ERR);
						return -2;
					}
					return 1;
				}
				Err.error(ErrEnum.FUNC_PARA_DECL_ERR);
				return -2;
			}
		}
		// ͷ���ż���ƥ��
		reToken(); // ����
		return -1;
	}

	// <parameter-declaration-list> ::=
	// <parameter-declaration>{','<parameter-declaration>}
	private int analyseParameterDeclarationList() {
		// ������������ͱ���һ�����в���
		if (analyseParameterDeclaration() == 1) {
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				if (token.getType() != TokenType.COMMA) {
					reToken();
					return 1;
				}
				if (analyseParameterDeclaration() != 1) {
					Err.error(ErrEnum.FUNC_PARA_DECL_ERR);
					return -2;
				}
				// ��ǰ�����������
			}
		}
		// û�в���������
		Err.error(ErrEnum.FUNC_PARA_DECL_ERR);
		return -2;
	}

	// <parameter-declaration> ::= [<const-qualifier>]<type-specifier><identifier>
	private int analyseParameterDeclaration() {
		token = getToken();
		if (token == null) {
			return 0;
		}
		// CONST����
		if (token.getType() == TokenType.CONST) {
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() == TokenType.INT) {
				// ��������+1
				paraNum++;
				token = getToken();
				if (token == null) {
					Err.error(ErrEnum.EOF_ERR);
					return -2;
				}
				if (token.getType() == TokenType.ID) {
					String name = token.getValue();
					// ����ض���
					if (block.containsID(name)) {
						Err.error(ErrEnum.ID_REDECL_ERR);
						return -2;
					}
					// ���������Ҫ��ϸ����
					block.put(0, name, offset++);
					return 1;
				}
				// ȱ��ID
				Err.error(ErrEnum.NEED_ID_ERR);
				return -2;
			}
			// ��֧�ֵ���������
			Err.error(ErrEnum.US_TYPE_ERR);
			return -2;
		}
		// ��CONST����
		else if (token.getType() == TokenType.INT) {
			// ��������+1
			paraNum++;
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() == TokenType.ID) {
				String name = token.getValue();
				// ����ض���
				if (block.containsID(name)) {
					Err.error(ErrEnum.ID_REDECL_ERR);
					return -2;
				}
				// ���������Ҫ��ϸ����
				// �������һ���ǻᱻ��ʼ����
				block.put(1, name, offset++);
				return 1;
			}
			// ȱ��ID
			Err.error(ErrEnum.NEED_ID_ERR);
			return -2;
		}
		// �ݶ���֧�ֵ����ݣ�����������
		Err.error(ErrEnum.US_TYPE_ERR);
		return -2;
	}

	// <compound-statement> ::= '{' {<variable-declaration>} <statement-seq> '}'
	private int analyseCompoundStatement(int fatherNo) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.LLB) {
			// {<variable-declaration>}
			// ֱ�ӻ�ȡ��ǰblock��no����
			int no = block.no;
			while (true) {
				if (analyseVariableDeclaration(no) != 1) {
					break;
				}
			}
			if (analyseStatementSeq(no) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.RLB) {
				Err.error(ErrEnum.RLB_ERR);
				return -2;
			}
			return 1;
		} else {
			reToken();
			return -1;
		}
	}

	// <statement-seq> ::= {<statement>}
	private int analyseStatementSeq(int no) {
		while (true) {
			if (analyseStatement(no) != 1) {
				break;
			}
		}
		return 1;
	}

	// <statement> ::=
	// '{' <statement-seq> '}'
	// |<condition-statement>
	// |<loop-statement>
	// |<jump-statement>
	// |<print-statement>
	// |<scan-statement>
	// |<assignment-expression>';'
	// |<function-call>';'
	// |';''
	private int analyseStatement(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.LLB) {
			if (analyseStatementSeq(no) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.RLB) {
				Err.error(ErrEnum.RLB_ERR);
				return -2;
			}
			return 1;
		}
		reToken();
		if (analyseConditionStatement(no) == 1) {
			return 1;
		} else if (analyseLoopStatement(no) == 1) {
			return 1;
		} else if (analyseJumpStatement(no) == 1) {
			return 1;
		} else if (analysePrintStatement(no) == 1) {
			return 1;
		} else if (analyseScanStatement(no) == 1) {
			return 1;
		}
		// ��ֵ���ͺ�������ͷ���ż���ͬ
		// ����token������Ϊ��
		token = getToken();
		if (token.getType() == TokenType.ID) {
			String name = token.getValue();
			reToken();
			// ����Ǻ���
			if (funcTable.containsFunc(name)) {
				int res = analyseFunctionCall(no);
				if (res == 1 || res == 2) {
					token = getToken();
					if (token == null) {
						Err.error(ErrEnum.EOF_ERR);
						return -2;
					}
					if (token.getType() != TokenType.SEM) {
						Err.error(ErrEnum.SEM_ERR);
						return -2;
					}
					return 1;
				}
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			// ������ñ���������
			else {
				if (analyseAssignmentExpression(no) == 1) {
					token = getToken();
					if (token == null) {
						Err.error(ErrEnum.EOF_ERR);
						return -2;
					}
					if (token.getType() != TokenType.SEM) {
						Err.error(ErrEnum.SEM_ERR);
						return -2;
					}
					return 1;
				}
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
		}
		// ;
		reToken();
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() != TokenType.SEM) {
			reToken();
			return -1;
		}
		return 1;

	}

	// <condition-statement> ::=
	// 'if' '(' <condition> ')' <statement> ['else' <statement>]
	private int analyseConditionStatement(int no) {
		JumpOffset JMP = new JumpOffset(0), LABEL1 = new JumpOffset(0), LABEL2 = new JumpOffset(0);
		token = getToken();
		if (token == null) {
			return 0;
		}
		// 'if'
		if (token.getType() == TokenType.IF) {
			// '('
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.LSB) {
				Err.error(ErrEnum.LSB_ERR);
				return -2;
			}
			// <condition>
			if (analyseCondition(no, JMP) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			// ')'
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.RSB) {
				Err.error(ErrEnum.RSB_ERR);
				return -2;
			}
			// <statement> stm1
			if (analyseStatement(no) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			token = getToken();
			if (token == null) {
				// ������label1��λ��
				LABEL1.label = text.getIndex() + 1;
				// ����jcondָ��
				text.reWrite(JMP.label, "", new Integer(LABEL1.label).toString(), "");
				return 1;
			}
			if (token.getType() != TokenType.ELSE) {
				reToken();
				// ������label1��λ��
				LABEL1.label = text.getIndex() + 1;
				// ����jcondָ��
				text.reWrite(JMP.label, "", new Integer(LABEL1.label).toString(), "");
				return 1;
			}
			// 'else'
			// �ȼ�һ����������תָ��
			// LABEL2���ÿ�
			text.addCode("jmp", "", "");
			// ����label1��λ��
			LABEL1.label = text.getIndex() + 1;
			// �����һ��jcondָ��
			text.reWrite(JMP.label, "", new Integer(LABEL1.label).toString(), "");
			// ��¼�ڶ���jmpָ���λ��
			JMP.label = text.getIndex();
			// <statement> stm2
			if (analyseStatement(no) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			// ��label2��λ��
			LABEL2.label = text.getIndex() + 1;
			// ����ڶ���jmpָ��
			text.reWrite(JMP.label, "", new Integer(LABEL2.label).toString(), "");
			return 1;
		}
		reToken();
		return -1;
	}

	// <condition> ::= <expression>[<relational-operator><expression>]
	private int analyseCondition(int no, JumpOffset JMP) {
		if (analyseExpression(no) == 1) {
			token = getToken();
			if (token == null) {
				return 1;
			}
			TokenType type = token.getType();
			if (type == TokenType.L || type == TokenType.LE || type == TokenType.G || type == TokenType.GE
					|| type == TokenType.UE || type == TokenType.EE) {
				if (analyseExpression(no) != 1) {
					Err.error(ErrEnum.FUNC_STATMENT_ERR);
					return -2;
				}
				text.addCode("isub", "", "");
				switch (type) {
				case L:
					text.addCode("jge", "", "");
					break;
				case LE:
					text.addCode("jg", "", "");
					break;
				case G:
					text.addCode("jle", "", "");
					break;
				case GE:
					text.addCode("jl", "", "");
					break;
				case UE:
					text.addCode("je", "", "");
					break;
				case EE:
					text.addCode("jne", "", "");
					break;
				default:
					Err.error(ErrEnum.SP_ERR);
					break;
				}
				JMP.label = text.getIndex();
				return 1;
			} else {
				reToken();
				// <condition> ::= <expression>
				// ����ջ������<expression>
				// ��ת��stm2
				// ��ʱ�Ȳ�дlabel
				text.addCode("je", "", "");
				// ��¼jmp����λ�ã�������
				JMP.label = text.getIndex();
				return 1;
			}
		}
		return -1;
	}

	// <loop-statement> ::= 'while' '(' <condition> ')' <statement>
	private int analyseLoopStatement(int no) {
		JumpOffset JMP = new JumpOffset(0), LABEL1 = new JumpOffset(0), LABEL2 = new JumpOffset(0);
		token = getToken();
		if (token == null) {
			return 0;
		}
		// while
		if (token.getType() == TokenType.WHILE) {
			// (
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.LSB) {
				Err.error(ErrEnum.LSB_ERR);
				return -2;
			}
			// ��¼label2�̶���ת
			LABEL2.label = text.getIndex() + 1;
			if (analyseCondition(no, JMP) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			// )
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.RSB) {
				Err.error(ErrEnum.RSB_ERR);
				return -2;
			}
			if (analyseStatement(no) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			text.addCode("jmp", new Integer(LABEL2.label).toString(), "");
			LABEL1.label = text.getIndex() + 1;
			text.reWrite(JMP.label, "", new Integer(LABEL1.label).toString(), "");
			return 1;
		}
		reToken();
		return -1;
	}

	// <jump-statement> ::= <return-statement>
	// <return-statement> ::= 'return' [<expression>] ';'
	// <jump-statement> ::= 'return' [<expression>] ';'
	private int analyseJumpStatement(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		// return
		if (token.getType() == TokenType.RETURN) {
			if (func.resType == TokenType.VOID) {
				token = getToken();
				if (token == null) {
					Err.error(ErrEnum.EOF_ERR);
					return -2;
				}
				if (token.getType() != TokenType.SEM) {
					Err.error(ErrEnum.SEM_ERR);
					return -2;
				}
				text.addCode("ret", "", "");
				return 1;
			} else if (func.resType == TokenType.INT) {
				if (analyseExpression(no) != 1) {
					Err.error(ErrEnum.EXP_ERR);
					return -2;
				}
				// ;
				token = getToken();
				if (token == null) {
					Err.error(ErrEnum.EOF_ERR);
					return -2;
				}
				if (token.getType() != TokenType.SEM) {
					Err.error(ErrEnum.SEM_ERR);
					return -2;
				}
				text.addCode("iret", "", "");
				return 1;
			} else {
				Err.error(ErrEnum.SP_ERR);
				return -2;
			}

		}
		reToken();
		return -1;
	}

	// <print-statement> ::= 'print' '(' [<printable-list>] ')' ';'
	private int analysePrintStatement(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() != TokenType.PRINT) {
			reToken();
			return -1;
		}
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.LSB) {
			Err.error(ErrEnum.LSB_ERR);
			return -2;
		}
		// Ԥ��
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() == TokenType.RSB) {
			return 1;
		}
		// ���б��ʽ
		reToken();
		if (analysePrintableList(no) != 1) {
			Err.error(ErrEnum.FUNC_STATMENT_ERR);
			return -2;
		}
		text.addCode("printl", "", "");
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.RSB) {
			Err.error(ErrEnum.RSB_ERR);
			return -2;
		}
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.SEM) {
			Err.error(ErrEnum.SEM_ERR);
			return -2;
		}
		return 1;
	}

	// <printable-list> ::= <printable> {',' <printable>}
	private int analysePrintableList(int no) {
		if (analysePrintable(no) == 1) {
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				if (token.getType() != TokenType.COMMA) {
					reToken();
					return 1;
				}
				// bipush 32 + cprint
				text.addCode("bipush", "32", "");
				text.addCode("cprint", "", "");
				if (analysePrintable(no) != 1) {
					Err.error(ErrEnum.FUNC_STATMENT_ERR);
					return -2;
				}
			}
		}
		return -1;
	}

	// <printable> ::= <expression>
	private int analysePrintable(int no) {
		if (analyseExpression(no) != 1) {
			return -1;
		}
		text.addCode("iprint", "", "");
		return 1;
	}

	// <scan-statement> ::= 'scan' '(' <identifier> ')' ';'
	private int analyseScanStatement(int no) {
		// scan
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() != TokenType.SCAN) {
			reToken();
			return -1;
		}
		// (
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.LSB) {
			Err.error(ErrEnum.LSB_ERR);
			return -2;
		}
		// id
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.ID) {
			Err.error(ErrEnum.NEED_ID_ERR);
			return -2;
		}
		String name = token.getValue();
		// )
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.RSB) {
			Err.error(ErrEnum.RSB_ERR);
			return -2;
		}
		// ;
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.SEM) {
			Err.error(ErrEnum.SEM_ERR);
			return -2;
		}
		// �����ַ��ջƫ��
		Integer res = table.getKind(name, no);
		// ������
		if (res == null) {
			Err.error(ErrEnum.ID_UNDECL_ERR);
			return -2;
		}
		// ����
		if (res == 0) {
			Err.error(ErrEnum.CONST_AS_ERR);
			return -2;
		}
		// ���ջƫ��
		Offset off = table.getOffset(name, no);
		// level = 1
		if (off.no == 0 && no != 0) {
			text.addCode("loada", "1", off.offset.toString());
		}
		// level = 0
		else {
			text.addCode("loada", "0", off.offset.toString());
		}
		// ��ѹջֵ
		text.addCode("iscan", "", "");
		// ���store
		text.addCode("istore", "", "");
		return 1;
	}

	// <assignment-expression> ::= <identifier>'='<expression>
	private int analyseAssignmentExpression(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() != TokenType.ID) {
			reToken();
			return -1;
		}
		// ���ص�ַ
		String name = token.getValue();
		Integer res = table.getKind(name, no);
		// ������
		if (res == null) {
			Err.error(ErrEnum.ID_UNDECL_ERR);
			return -2;
		}
		// ����
		if (res == 0) {
			Err.error(ErrEnum.CONST_AS_ERR);
			return -2;
		}
		// ���ջƫ��
		Offset off = table.getOffset(name, no);
		// level = 1
		if (off.no == 0 && no != 0) {
			text.addCode("loada", "1", off.offset.toString());
		}
		// level = 0
		else {
			text.addCode("loada", "0", off.offset.toString());
		}
		// =
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.E) {
			Err.error(ErrEnum.SP_ERR);
			return -2;
		}
		// <expression>
		if (analyseExpression(no) != 1) {
			Err.error(ErrEnum.FUNC_STATMENT_ERR);
			return -2;
		}
		// ��������ʽ��ֵ�Ѿ���ѹջ��
		text.addCode("istore", "", "");
		// �����δ��ʼ���ı����������״̬
		if (res == -1) {
			table.getBlock(off.no).change(name);
		}
		return 1;
	}

	// <function-call> ::=
	// <identifier> '(' [<expression-list>] ')'
	// ����Ƚ����⣬�漰������ֵ���͵�����
	// VOID ����1
	// INT ����2
	private int analyseFunctionCall(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.ID) {
			String name = token.getValue();
			// �����Ƿ��иú���
			if (!funcTable.containsFunc(name)) {
				Err.error(ErrEnum.ID_UNDECL_ERR);
				return -2;
			}
			// ��øú���������
			Func temp = funcTable.getFunc(name);
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.LSB) {
				Err.error(ErrEnum.LSB_ERR);
				return -2;
			}
			// Ԥ���ж��Ƿ��в����б�
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			// û�в����б�
			if (token.getType() == TokenType.RSB) {
				reToken();
				// �����������Ƿ��Ӧ
				if (temp.paraNum != 0) {
					Err.error(ErrEnum.FUNC_PARA_ERR);
					return -2;
				}
				// ��ú������ϵ�λ��
				Integer funcIndex = funcTable.getIndex(name);
				// ���ڽ�ȫ�ֱ��������Ĵ���Ҳ����һ����������������funcIndex���������1
				// ��ӵ������
				text.addCode("call", new Integer(funcIndex - 1).toString(), "");
			}
			// �в����б�
			else {
				reToken();
				// �������ṩ�Ĳ�������
				callParaNum = 0;
				if (analyseExpressionList(no) != 1) {
					Err.error(ErrEnum.FUNC_STATMENT_ERR);
					return -2;
				}
				// �����������Ƿ��Ӧ
				if (temp.paraNum != callParaNum) {
					Err.error(ErrEnum.FUNC_PARA_ERR);
					return -2;
				}
				// ��ú������ϵ�λ��
				Integer funcIndex = funcTable.getIndex(name);
				// ����ͬ��
				// ��ӵ������
				text.addCode("call", new Integer(funcIndex - 1).toString(), "");
			}
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.RSB) {
				Err.error(ErrEnum.RSB_ERR);
				return -2;
			}
			if (temp.resType == TokenType.VOID) {
				return 1;
			} else if (temp.resType == TokenType.INT) {
				return 2;
			}
		}
		reToken();
		return -1;
	}

	// <expression-list> ::=
	// <expression>{','<expression>}
	private int analyseExpressionList(int no) {
		if (analyseExpression(no) == 1) {
			callParaNum++;
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				if (token.getType() != TokenType.COMMA) {
					reToken();
					return 1;
				}
				if (analyseExpression(no) != 1) {
					Err.error(ErrEnum.FUNC_STATMENT_ERR);
					return -2;
				}
				callParaNum++;
			}
		}
		return -1;
	}
}
