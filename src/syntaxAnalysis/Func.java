package syntaxAnalysis;

import java.util.ArrayList;

import lexicalAnalysis.TokenType;
import out.Text;

//������Ϊ����һ��value���������ű�����ö����õļ���
class Func {

	// ������������
	TokenType resType;
	// ���������б�
	// �жϲ����б���ͬʱ��list1.containsAll(list2) && list2.containsAll(list1)
	ArrayList<TokenType> paraList = new ArrayList<TokenType>();
	// ��������ı�
	Text funcText;

	Func(TokenType resType, ArrayList<TokenType> paraList, Text funcText) {
		this.resType = resType;
		this.paraList = paraList;
		this.funcText = funcText;
	}
}
