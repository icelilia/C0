package syntaxAnalysis;

import java.util.ArrayList;

class Table {
	private ArrayList<Block> blockList = new ArrayList<Block>();

	Table() {
	}

	void addBlock(Block block) {
		this.blockList.add(block);
	}

	Block getBlock(int no) {
		return blockList.get(no);
	}

	// ���������漰���㼶����
	// ������������������ȫ�֣�ȫ�ֵ�ȫ��
	Offset getOffset(String name, int no) {
		// �Ѿ���ȫ��������
		if (no == 0) {
			Block block = blockList.get(no);
			return new Offset(block.getOffset(name), no);
		} else {
			// ���ڱ�Block�в���
			Block block = blockList.get(no);
			Integer offset = block.getOffset(name);
			if (offset != null) {
				return new Offset(offset, no);
			}
			// �Ҳ������ڸ�Block�в���
			else {
				return getOffset(name, block.fatherNo);
			}
		}
	}

	// ���ظñ�ʶ������
	// 0������
	// 1���ѳ�ʼ������
	// -1��δ��ʼ������
	Integer getKind(String name, int no) {
		if (no == 0) {
			Block block = blockList.get(no);
			return block.getKind(name);
		} else {
			// ���ڱ�Block�в���
			Block block = blockList.get(no);
			Integer kind = block.getKind(name);
			if (kind != null) {
				return kind;
			}
			// �Ҳ������ڸ�Block�в���
			else {
				return getKind(name, block.fatherNo);
			}
		}
	}
}
