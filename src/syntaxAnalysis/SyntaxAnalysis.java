package syntaxAnalysis;

import java.util.ArrayList;

import lexicalAnalysis.Token;
import lexicalAnalysis.TokenType;
import out.Output;
import out.Text;
import error.*;

public class SyntaxAnalysis {

	private ArrayList<Token> tokenList;
	private int index = 0; // ��һ��Ҫȡ��Token���
	private Token token; // ȫ��ʹ�õ�token

	// ������
	private FuncTable funcTable = new FuncTable();
	// ������������
	private TokenType resType;
	// ���������б�
	private ArrayList<TokenType> paraList;

	// ����
	private Block block;
	// ������
	private int NO = 1;
	// ������
	private Table table = new Table();

	// ջƫ��
	private int offset;

	// ��Ҫ�����jmp����index
	private int jmp;
	// stm1��index
	private int label1;
	// stm2��index
	private int label2;

	// ָ���ı�
	private Text text;
	// ����ļ��б�
	private Output output = new Output();

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
	public void syntaxAnalysis() {
		if (analyseC0Program() == 1) {
			System.out.println("�﷨�������");
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
				Err.error(ErrEnum.ID_ERR);
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
		Err.error(ErrEnum.UK_TYPE_ERR);
		return -2;
	}

	// <C0-program> ::= {<variable-declaration>}{<function-definition>}
	private int analyseC0Program() {
		// {<variable-declaration>}
		// Ϊȫ�ֳ��������½�һ��Block�����Ϊ0�������Ϊ-1
		block = new Block(0, -1);
		table.addBlock(block);
		// Ϊȫ�ֳ��������ĳ�ʼ�������½������
		text = new Text("ȫ��"); // �����ĵ����֣���ֹ�ͺ���ĺ�������
		output.addText(text);
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
				Err.error(ErrEnum.UK_TYPE_ERR);
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
			if (block.containsKey(name)) {
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
			if (block.containsKey(name)) {
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
			// ���ջƫ��
			Offset off = table.getOffset(name, no);
			// �ȿ��Ƿ��������ʶ��
			if (off.offset == null) {
				Err.error(ErrEnum.ID_UNDECL_ERR);
				return -2;
			}
			// �ٿ��Ƿ��ѳ�ʼ��
			if (table.getBlock(no).isUnInit(name)) {
				Err.error(ErrEnum.VAR_UNINIT_ERR);
				return -2;
			}
			// level = 1
			if (off.no == 0 && no != 0) {
				text.addCode("loada", "1", off.offset.toString());
			}
			// level = 0
			else {
				text.addCode("loada", "0", off.offset.toString());
			}
			return 1;
		}
		// <integer-literal>
		else if (token.getType() == TokenType.DEC_INT) {
			text.addCode("ipush", token.getValue(), "");
			return 1;
		}
		// ��ʱ�Ȳ����Ǻ�������
		// else if (analyseFunctionCall() == 1) {
		//
		// }
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
				if (funcTable.containsKey(name)) {
					Err.error(ErrEnum.FUNC_REDECL_ERR);
					return -2;
				}
				// ����������Ӹú���
				funcTable.put(name, null);
				int no = NO++;
				// ���ｫ��������������������һ����
				// ���Բ�����ĸ���һ����ȫ�ֿ�0
				block = new Block(no, 0);
				table.addBlock(block);
				text = new Text(name);
				output.addText(text);
				offset = 0;
				// ��ʼ���������б�
				// ע�⣬����Ҳ�����ֲ���������
				if (analyseParameterClause() == 1) {
					// �����ֶ�����fatherNo
					if (analyseCompoundStatement(no) == 1) {
						// ���������Ϻ�������Ϣ�������뺯������
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
				Err.error(ErrEnum.ID_ERR);
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
		// �½������б�
		paraList = new ArrayList<TokenType>();
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
				// �����б�����Ӳ�������
				paraList.add(TokenType.INT);
				token = getToken();
				if (token == null) {
					Err.error(ErrEnum.EOF_ERR);
					return -2;
				}
				if (token.getType() == TokenType.ID) {
					String name = token.getValue();
					// ����ض���
					if (block.containsKey(name)) {
						Err.error(ErrEnum.ID_REDECL_ERR);
						return -2;
					}
					// ���������Ҫ��ϸ����
					block.put(0, name, offset++);
					return 1;
				}
				// ȱ��ID
				Err.error(ErrEnum.ID_ERR);
				return -2;
			}
			// ��֧�ֵ���������
			Err.error(ErrEnum.UK_TYPE_ERR);
			return -2;
		}
		// ��CONST����
		else if (token.getType() == TokenType.INT) {
			// �����б�����Ӳ���
			paraList.add(TokenType.INT);
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() == TokenType.ID) {
				String name = token.getValue();
				// ����ض���
				if (block.containsKey(name)) {
					Err.error(ErrEnum.ID_REDECL_ERR);
					return -2;
				}
				// ���������Ҫ��ϸ����
				// �������һ���ǻᱻ��ʼ����
				block.put(1, name, offset++);
				return 1;
			}
			// ȱ��ID
			Err.error(ErrEnum.ID_ERR);
			return -2;
		}
		// �ݶ���֧�ֵ����ݣ�����������
		Err.error(ErrEnum.UK_TYPE_ERR);
		return -2;
	}

	// <compound-statement> ::= '{' {<variable-declaration>} <statement-seq> '}'
	private int analyseCompoundStatement(int fatherNo) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.LLB) {
			int no = NO++;
			// �½���
			block = new Block(no, fatherNo);
			table.addBlock(block);
			// {<variable-declaration>}
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
			// ����block
			block = table.getBlock(block.fatherNo);
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
	// <compound-statement>
	// |<condition-statement>
	// |<loop-statement>
	// |<jump-statement>
	// |<print-statement>
	// |<scan-statement>
	// |<assignment-expression>';'
	// |<function-call>';'
	// |';'
	private int analyseStatement(int no) {
		if (analyseCompoundStatement(no) == 1) {
			return 1;
		} else if (analyseConditionStatement(no) == 1) {
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
		// �ٵ����жϺ����Ƿ��зֺ�
		else if (analyseAssignmentExpression(no) == 1) {
			return 1;
		}
		// �ٵ����жϺ����Ƿ��зֺ�
		else if (analyseFunctionCall(no) == 1) {
			return 1;
		} else {

		}
		return -1;
	}

	// <condition-statement> ::=
	// 'if' '(' <condition> ')' <statement> ['else' <statement>]
	private int analyseConditionStatement(int no) {
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
			if (analyseCondition(no) != 1) {
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
				label1 = text.getIndex() + 1;
				// ����jcondָ��
				text.reWrite(jmp, "", new Integer(label1).toString(), "");
				return 1;
			}
			if (token.getType() != TokenType.ELSE) {
				reToken();
				// ������label1��λ��
				label1 = text.getIndex() + 1;
				// ����jcondָ��
				text.reWrite(jmp, "", new Integer(label1).toString(), "");
				return 1;
			}
			// 'else'
			// �ȼ�һ����������תָ��
			// label2���ÿ�
			text.addCode("jmp", "", "");
			// ����label1��λ��
			label1 = text.getIndex() + 1;
			// �����һ��jcondָ��
			text.reWrite(jmp, "", new Integer(label1).toString(), "");
			// ��¼�ڶ���jmpָ���λ��
			jmp = text.getIndex();
			// <statement> stm2
			if (analyseStatement(no) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			// ��label2��λ��
			label2 = text.getIndex() + 1;
			// ����ڶ���jmpָ��
			text.reWrite(jmp, "", new Integer(label2).toString(), "");
			return 1;
		}
		reToken();
		return -1;
	}

	// <condition> ::= <expression>[<relational-operator><expression>]
	private int analyseCondition(int no) {
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
				jmp = text.getIndex();
				return 1;
			} else {
				reToken();
				// <condition> ::= <expression>
				// ����ջ������<expression>
				// ��ת��stm2
				// ��ʱ�Ȳ�дlabel
				text.addCode("je", "", "");
				// ��¼jmp����λ�ã�������
				jmp = text.getIndex();
				return 1;
			}
		}
		return -1;
	}

	private int analyseLoopStatement(int no) {
		return -1;
	}

	private int analyseJumpStatement(int no) {
		return -1;
	}

	private int analysePrintStatement(int no) {
		return -1;
	}

	private int analyseScanStatement(int no) {
		return -1;
	}

	private int analyseAssignmentExpression(int no) {
		return -1;
	}

	private int analyseFunctionCall(int no) {
		return -1;
	}
}
