package lexicalAnalysis;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;

import error.*;

public class LexicalAnalysis {
	BufferedInputStream inputStream;

	char ch;
	char temp;
	String tokenString;
	int num;
	Token token;

	int row = 1;
	int col = 1;

	boolean isComment;

	DFAState curruntState;

	ArrayList<Token> tokenList = new ArrayList<Token>(512);

	public LexicalAnalysis(FileInputStream fileInputStream) {
		this.inputStream = new BufferedInputStream(fileInputStream);
	}

	public ArrayList<Token> lexicalAnalysis() {
		// ��Ϊ0��ʾ��û��EOF
		while (true) {
			curruntState = DFAState.INIT_STATE;
			if (getToken() == 0) {
				tokenList.add(token);
				break;
			} else {
				tokenList.add(token);
			}
		}
		return tokenList;
	}

	private char getchar() {
		char temp = '\0';
		try {
			inputStream.mark(1);
			temp = (char) inputStream.read();
		} catch (IOException e) {
			System.err.println("��������ȡ����");
			System.exit(-1);
		}
		return temp;
	}

	private void rechar() {
		try {
			inputStream.reset();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private boolean isSpace(char c) {
		return c == ' ';
	}

	private boolean isCR(char c) {
		return c == '\r';
	}

	private boolean isLF(char c) {
		return c == '\n';
	}

	private boolean isTab(char c) {
		return c == '\t';
	}

	private boolean isEOF(char c) {
		return c == (char) -1;
	}

	private boolean isLetter(char c) {
		return Character.isLetter(c);
	}

	private boolean isDigit(char c) {
		return Character.isDigit(c);
	}

	private boolean isHex(char c) {
		// A-F��a-f��0-9
		return (65 <= c && c <= 70) || (97 <= c && c <= 102) || isDigit(c);
	}

	// int����ֵ��-1��ʾ����0��ʾ����EOF��1��ʾ������ȡ
	// ʵ������ò���-1����Ϊһ�������ֱ�ӵ���Err.error���д���������ִ��System.exit(-1)
	private int getToken() {
		// ��ʼ��
		tokenString = "";
		while (true) {
			ch = getchar();
			switch (curruntState) {
			case INIT_STATE: {
				if (isEOF(ch)) {
					return 0;
				}
				// �հ��ַ�ֱ������
				if (isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch)) {
					break;
				}
				// Ϊ����
				else if (isDigit(ch)) {
					// ����0��ͷ��ʮ��������
					if (ch != '0') {
						curruntState = DFAState.DEC_INT_STATE;
					}
					// 0��ͷ��ʮ������������ʮ����0
					else {
						ch = getchar();
						// ʮ����������
						if (ch == 'x' || ch == 'X') {
							// ��tokenString�����"0x"
							// tokenString = tokenString + "0x";
							// Integer.valueOf()������������ַ�����Ҫ�����"0x"
							// ������Ԥ��һ���ַ���֤"0x"�����������
							ch = getchar();
							if (isHex(ch)) {
								curruntState = DFAState.HEX_INT_STATE;
							}
							// �ʷ���Ҫ��������һλ���֣�û�������򱨴�
							else {
								Err.error(ErrEnum.INPUT_ERR);
							}
						}
						// ����������һ��ʮ����0
						else {
							rechar();
							tokenString = tokenString + '0';
							token = new Token(TokenType.DEC_INT, tokenString);
							return isEOF(ch) ? 0 : 1;
						}
					}
				}
				// ΪӢ����ĸ
				else if (isLetter(ch)) {
					curruntState = DFAState.ID_STATE;
				}
				// ��������
				else {
					switch (ch) {
					case '+':
						curruntState = DFAState.PLUS_STATE;
						break;
					case '-':
						curruntState = DFAState.MINUS_STATE;
						break;
					case '*':
						curruntState = DFAState.MUL_STATE;
						break;
					case '/':
						curruntState = DFAState.DIV_STATE;
						break;
					case '=':
						// Ԥ��
						temp = getchar();
						// ˫�Ⱥ�
						if (temp == '=') {
							// Ԥ����ӵ�һ���ַ�
							tokenString = tokenString + ch;
							ch = temp;
							curruntState = DFAState.EE_STATE;
						}
						// ���Ⱥţ�����
						else {
							rechar();
							curruntState = DFAState.E_STATE;
						}
						break;
					case '<':
						// Ԥ��
						temp = getchar();
						// С�ڵ��ں�
						if (temp == '=') {
							tokenString = tokenString + ch;
							ch = temp;
							curruntState = DFAState.LE_STATE;
							break;
						}
						// С�ںţ�����
						else {
							rechar();
							curruntState = DFAState.L_STATE;
							break;
						}
					case '>':
						// Ԥ��
						temp = getchar();
						// ���ڵ��ں�
						if (temp == '=') {
							tokenString = tokenString + ch;
							ch = temp;
							curruntState = DFAState.GE_STATE;
							break;
						}
						// ���ںţ�����
						else {
							rechar();
							curruntState = DFAState.G_STATE;
							break;
						}
					case '!':
						// Ԥ��
						temp = getchar();
						// �����ں�
						if (temp == '=') {
							tokenString = tokenString + ch;
							ch = temp;
							curruntState = DFAState.UE_STATE;
							break;
						}
						// �쳣�ַ���ϣ�����
						else {
							Err.error(ErrEnum.INPUT_ERR);
							break;
						}
					case ',':
						curruntState = DFAState.COMMA_STATE;
						break;
					case ';':
						curruntState = DFAState.SEM_STATE;
						break;
					case '(':
						curruntState = DFAState.LSB_STATE;
						break;
					case ')':
						curruntState = DFAState.RSB_STATE;
						break;
					case '{':
						curruntState = DFAState.LLB_STATE;
						break;
					case '}':
						curruntState = DFAState.RLB_STATE;
						break;
					// ����Ƿ��ַ�
					default:
						Err.error(ErrEnum.INPUT_ERR);
						break;
					}
				}
				// ״̬�����ı䣬����ַ�
				if (curruntState != DFAState.INIT_STATE) {
					tokenString = tokenString + ch;
				}
				break;
			}
			// miniplc0�У��ڴ�״̬�¶�����ĸʱѡ����ת����ʶ��״̬
			// ����ѡ����Ӵʷ�����������ɵ�ԭ�򣬲�����ת
			case DEC_INT_STATE: {
				if (isEOF(ch)) {
					try {
						num = Integer.parseInt(tokenString);
					} catch (NumberFormatException e) {
						Err.error(ErrEnum.INT_OF_ERR);
						break;
					}
					token = new Token(TokenType.DEC_INT, tokenString);
					return 0;
				}
				// ����������ֱ�Ӻϲ�
				if (isDigit(ch)) {
					tokenString = tokenString + ch;
				}
				// ��������������ַ�������
				else {
					rechar();
					try {
						num = Integer.parseInt(tokenString);
					} catch (NumberFormatException e) {
						Err.error(ErrEnum.INT_OF_ERR);
						break;
					}
					token = new Token(TokenType.DEC_INT, tokenString);
					return 1;
				}
				break;
			}
			// ʮ�����ƻ���ͬʮ����
			case HEX_INT_STATE: {
				if (isEOF(ch)) {
					try {
						// ʮ�����Ƶ�ֱ��ת��
						num = Integer.valueOf(tokenString, 16);
					} catch (NumberFormatException e) {
						Err.error(ErrEnum.INT_OF_ERR);
						break;
					}
					// ����ѡ��ֱ�ӽ�ʮ������ת����ʮ����
					token = new Token(TokenType.DEC_INT, new Integer(num).toString());
					return 0;
				}
				// ����ʮ�����ƺϷ��ַ���ֱ�Ӻϲ�
				if (isHex(ch)) {
					tokenString = tokenString + ch;
				}
				// ��������������ַ�������
				else {
					rechar();
					try {
						// ʮ�����Ƶ�ֱ��ת��
						num = Integer.valueOf(tokenString, 16);
					} catch (NumberFormatException e) {
						Err.error(ErrEnum.INT_OF_ERR);
						break;
					}
					// ����ѡ��ֱ�ӽ�ʮ������ת����ʮ����
					token = new Token(TokenType.DEC_INT, new Integer(num).toString());
					return 1;
				}
				break;
			}
			case ID_STATE: {
				if (isEOF(ch)) {
					// �Ƚϱ�����
					if (tokenString.contentEquals("const")) {
						token = new Token(TokenType.CONST, tokenString);
					} else if (tokenString.contentEquals("void")) {
						token = new Token(TokenType.VOID, tokenString);
					} else if (tokenString.contentEquals("int")) {
						token = new Token(TokenType.INT, tokenString);
					} else if (tokenString.contentEquals("char")) {
						token = new Token(TokenType.CHAR, tokenString);
					} else if (tokenString.contentEquals("double")) {
						token = new Token(TokenType.DOUBLE, tokenString);
					} else if (tokenString.contentEquals("struct")) {
						token = new Token(TokenType.STRUCT, tokenString);
					} else if (tokenString.contentEquals("if")) {
						token = new Token(TokenType.IF, tokenString);
					} else if (tokenString.contentEquals("else")) {
						token = new Token(TokenType.ELSE, tokenString);
					} else if (tokenString.contentEquals("switch")) {
						token = new Token(TokenType.SWITCH, tokenString);
					} else if (tokenString.contentEquals("case")) {
						token = new Token(TokenType.CASE, tokenString);
					} else if (tokenString.contentEquals("default")) {
						token = new Token(TokenType.DEFAULT, tokenString);
					} else if (tokenString.contentEquals("while")) {
						token = new Token(TokenType.WHILE, tokenString);
					} else if (tokenString.contentEquals("for")) {
						token = new Token(TokenType.FOR, tokenString);
					} else if (tokenString.contentEquals("do")) {
						token = new Token(TokenType.DO, tokenString);
					} else if (tokenString.contentEquals("return")) {
						token = new Token(TokenType.RETURN, tokenString);
					} else if (tokenString.contentEquals("break")) {
						token = new Token(TokenType.BREAK, tokenString);
					} else if (tokenString.contentEquals("continue")) {
						token = new Token(TokenType.CONTINUE, tokenString);
					} else if (tokenString.contentEquals("print")) {
						token = new Token(TokenType.PRINT, tokenString);
					} else if (tokenString.contentEquals("scan")) {
						token = new Token(TokenType.SCAN, tokenString);
					} else {
						token = new Token(TokenType.ID, tokenString);
					}
					return 0;
				}
				if (isDigit(ch) || isLetter(ch)) {
					tokenString = tokenString + ch;
				} else {
					rechar();
					if (tokenString.contentEquals("const")) {
						token = new Token(TokenType.CONST, tokenString);
					} else if (tokenString.contentEquals("void")) {
						token = new Token(TokenType.VOID, tokenString);
					} else if (tokenString.contentEquals("int")) {
						token = new Token(TokenType.INT, tokenString);
					} else if (tokenString.contentEquals("char")) {
						token = new Token(TokenType.CHAR, tokenString);
					} else if (tokenString.contentEquals("double")) {
						token = new Token(TokenType.DOUBLE, tokenString);
					} else if (tokenString.contentEquals("struct")) {
						token = new Token(TokenType.STRUCT, tokenString);
					} else if (tokenString.contentEquals("if")) {
						token = new Token(TokenType.IF, tokenString);
					} else if (tokenString.contentEquals("else")) {
						token = new Token(TokenType.ELSE, tokenString);
					} else if (tokenString.contentEquals("switch")) {
						token = new Token(TokenType.SWITCH, tokenString);
					} else if (tokenString.contentEquals("case")) {
						token = new Token(TokenType.CASE, tokenString);
					} else if (tokenString.contentEquals("default")) {
						token = new Token(TokenType.DEFAULT, tokenString);
					} else if (tokenString.contentEquals("while")) {
						token = new Token(TokenType.WHILE, tokenString);
					} else if (tokenString.contentEquals("for")) {
						token = new Token(TokenType.FOR, tokenString);
					} else if (tokenString.contentEquals("do")) {
						token = new Token(TokenType.DO, tokenString);
					} else if (tokenString.contentEquals("return")) {
						token = new Token(TokenType.RETURN, tokenString);
					} else if (tokenString.contentEquals("break")) {
						token = new Token(TokenType.BREAK, tokenString);
					} else if (tokenString.contentEquals("continue")) {
						token = new Token(TokenType.CONTINUE, tokenString);
					} else if (tokenString.contentEquals("print")) {
						token = new Token(TokenType.PRINT, tokenString);
					} else if (tokenString.contentEquals("scan")) {
						token = new Token(TokenType.SCAN, tokenString);
					} else {
						token = new Token(TokenType.ID, tokenString);
					}
					return 1;
				}
				break;
			}
			case PLUS_STATE: {
				rechar();
				token = new Token(TokenType.PLUS, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case MINUS_STATE: {
				rechar();
				token = new Token(TokenType.MINUS, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case MUL_STATE: {
				rechar();
				token = new Token(TokenType.MUL, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case DIV_STATE: {
				rechar();
				token = new Token(TokenType.DIV, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case E_STATE: {
				rechar();
				token = new Token(TokenType.E, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case L_STATE: {
				rechar();
				token = new Token(TokenType.L, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case LE_STATE: {
				rechar();
				token = new Token(TokenType.LE, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case G_STATE: {
				rechar();
				token = new Token(TokenType.G, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case GE_STATE: {
				rechar();
				token = new Token(TokenType.GE, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case UE_STATE: {
				rechar();
				token = new Token(TokenType.UE, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case EE_STATE: {
				rechar();
				token = new Token(TokenType.EE, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case COMMA_STATE: {
				rechar();
				token = new Token(TokenType.COMMA, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case SEM_STATE: {
				rechar();
				token = new Token(TokenType.SEM, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case LSB_STATE: {
				rechar();
				token = new Token(TokenType.LSB, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case RSB_STATE: {
				rechar();
				token = new Token(TokenType.RSB, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case LLB_STATE: {
				rechar();
				token = new Token(TokenType.LLB, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case RLB_STATE: {
				rechar();
				token = new Token(TokenType.RLB, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			default: {
				Err.error(ErrEnum.INPUT_ERR);
				break;
			}
			}
		}
	}
}