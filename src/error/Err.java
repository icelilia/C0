package error;

public class Err {

	public static void error(ErrEnum err) {
		switch (err) {
		case PARA_ERR:
			System.out.println("��������");
			System.exit(-1);
			break;
		case INPUT_FILE_ERR:
			System.out.println("�����ļ�������");
			System.exit(-1);
			break;
		case OUTPUT_FILE_ERR:
			System.out.println("�޷���������ļ�");
			System.exit(-1);
			break;
		case INPUT_ERR:
			System.out.println("���뺬�зǷ��ַ�����ʮ������������������ʽ����");
			System.exit(-1);
			break;
		case OUTPUT_ERR:
			System.out.println("��������ļ�д�����");
			System.exit(-1);
			break;
		case INT_OF_ERR:
			System.out.println("32λ�������������");
			System.exit(-1);
			break;
		case SP_ERR:
			System.out.println("������󣬽�������");
			System.exit(-1);
			break;
		case EOF_ERR:
			System.out.println("������;����EOF");
			System.exit(-1);
			break;
		case ID_ERR:
			System.out.println("ID����ȱ�ٱ�ʶ��");
			System.exit(-1);
			break;
		case UK_TYPE_ERR:
			System.out.println("��֧�ֵ���������");
			System.exit(-1);
			break;
		case CONST_DECL_ERR:
			System.out.println("��������ʱ�﷨����");
			System.exit(-1);
			break;
		case CONST_INIT_ERR:
			System.out.println("����δ����ʽ�س�ʼ��");
			System.exit(-1);
			break;
		case VAR_DECL_ERR:
			System.out.println("��������ʱ�﷨����");
			System.exit(-1);
			break;
		case ID_REDECL_ERR:
			System.out.println("��ʶ���ض���");
			System.exit(-1);
			break;
		case SEM_ERR:
			System.out.println("ȱ�ٷֺ�");
			System.exit(-1);
			break;
		case EXP_ERR:
			System.out.println("���ʽ�д��ڴ���");
			System.exit(-1);
			break;
		case ID_UNDECL_ERR:
			System.out.println("��ʶ��δ����");
			System.exit(-1);
			break;
		case VAR_UNINIT_ERR:
			System.out.println("ʹ����δ��ʼ���ı���");
			System.exit(-1);
			break;
		case FUNC_REDECL_ERR:
			System.out.println("�����ض���");
			System.exit(-1);
			break;
		case LSB_ERR:
			System.out.println("ȱ����-С����");
			System.exit(-1);
			break;
		case RSB_ERR:
			System.out.println("ȱ����-С����");
			System.exit(-1);
			break;
		case FUNC_PARA_DECL_ERR:
			System.out.println("������������ʱ�﷨����");
			System.exit(-1);
			break;
		case LLB_ERR:
			System.out.println("ȱ����-������");
			System.exit(-1);
			break;
		case RLB_ERR:
			System.out.println("ȱ����-������");
			System.exit(-1);
			break;
		case FUNC_STATMENT_ERR:
			System.out.println("��������﷨����");
			System.exit(-1);
			break;
		default:
			System.out.println("δ����Ĵ���");
			System.exit(-1);
			break;
		}
	}
}
