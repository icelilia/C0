package syntaxAnalysis;

import lexicalAnalysis.TokenType;

//������Ϊ����һ��value���������ű�����ö����õļ���
public class Func {

	// ������
	public String name;
	// ������������
	public TokenType resType;
	// ������������
	public Integer paraNum;
	// ��������ı�
	public Text text;

	Func(String name, TokenType resType, Integer paraNum) {
		this.name = name;
		this.resType = resType;
		this.paraNum = paraNum;
	}

	public void addText(Text text) {
		this.text = text;
	}
}
