package syntaxAnalysis;

import java.util.ArrayList;

public class Text {
	private String name;
	private int index = 0;
	private ArrayList<Code> codeList = new ArrayList<Code>();

	// name��Ϊ������
	Text(String name) {
		this.name = name;
	}

	// ���ָ��
	void addCode(String opcode, String operands1, String operands2) {
		Code code = new Code(index++, opcode, operands1, operands2);
		codeList.add(code);
	}

	// ���ڻ���
	// �õ�����ָ���index
	int getIndex() {
		//
		if (codeList.size() == 0) {
			return -1;
		}
		return codeList.get(codeList.size() - 1).index;
	}

	// ����index������ָ��
	void reWrite(int index, String opcode, String operands1, String operands2) {
		Code code = codeList.get(index);
		if (!opcode.contentEquals("")) {
			code.opcode = opcode;
		}
		if (!operands1.contentEquals("")) {
			code.operands1 = operands1;
		}
		if (!operands2.contentEquals("")) {
			code.operands2 = operands2;
		}
	}

	// �õ�����
	public String getName() {
		return this.name;
	}

	// ����ָ��
	public ArrayList<Code> getCodesList() {
		return codeList;
	}
}
