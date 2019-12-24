package error;

public class Err {

	public static void error(ErrEnum err) {
		switch (err) {
		case CLI_PARA_ERR:
			System.out.println("���������в�������");
			System.exit(-2);
			break;
		case INPUT_FILE_ERR:
			System.out.println("���������ļ�������");
			System.exit(-2);
			break;
		case OUTPUT_FILE_ERR:
			System.out.println("���󣺴�������ļ�ʧ��");
			System.exit(-2);
			break;
		case INPUT_ERR:
			System.out.println("�������뺬�зǷ��ַ�����ʮ������������������ʽ����");
			System.exit(-2);
			break;
		case OUTPUT_ERR:
			System.out.println("�����ļ�д�����");
			System.exit(-2);
			break;
		case INT_OF_ERR:
			System.out.println("����32λ�������������");
			System.exit(-2);
			break;
		case SP_ERR:
			System.out.println("����������󣬽�������");
			System.exit(-2);
			break;
		case EOF_ERR:
			System.out.println("���󣺷�����;����EOF");
			System.exit(-2);
			break;
		case NEED_ID_ERR:
			System.out.println("����ȱ�ٱ�ʶ��");
			System.exit(-2);
			break;
		case US_TYPE_ERR:
			System.out.println("���󣺲�֧�ֵ���������");
			System.exit(-2);
			break;
		case CONST_DECL_ERR:
			System.out.println("���󣺳�������ʱ�﷨����");
			System.exit(-2);
			break;
		case CONST_INIT_ERR:
			System.out.println("���󣺳���δ����ʽ�س�ʼ��");
			System.exit(-2);
			break;
		case CONST_AS_ERR:
			System.out.println("���󣺳����޷����ٴθ�ֵ");
			System.exit(-2);
			break;
		case VAR_DECL_ERR:
			System.out.println("���󣺱�������ʱ�﷨����");
			System.exit(-2);
			break;
		case ID_REDECL_ERR:
			System.out.println("���󣺱������ض���");
			System.exit(-2);
			break;
		case SEM_ERR:
			System.out.println("����ȱ�ٷֺ�");
			System.exit(-2);
			break;
		case EXP_ERR:
			System.out.println("���󣺱��ʽ�д��ڴ���");
			System.exit(-2);
			break;
		case ID_UNDECL_ERR:
			System.out.println("���󣺱�ʶ��δ����");
			System.exit(-2);
			break;
		case VAR_UNINIT_ERR:
			System.out.println("����ʹ����δ��ʼ���ı���");
			System.exit(-2);
			break;
		case FUNC_REDECL_ERR:
			System.out.println("���󣺺����ض���");
			System.exit(-2);
			break;
		case LSB_ERR:
			System.out.println("����ȱ����-С����");
			System.exit(-2);
			break;
		case RSB_ERR:
			System.out.println("����ȱ����-С����");
			System.exit(-2);
			break;
		case FUNC_PARA_DECL_ERR:
			System.out.println("���󣺺�����������ʱ�﷨����");
			System.exit(-2);
			break;
		case LLB_ERR:
			System.out.println("����ȱ����-������");
			System.exit(-2);
			break;
		case RLB_ERR:
			System.out.println("����ȱ����-������");
			System.exit(-2);
			break;
		case FUNC_STATMENT_ERR:
			System.out.println("���󣺺�������﷨����");
			System.exit(-2);
			break;
		case FUNC_PARA_ERR:
			System.out.println("���󣺵��ú���ʱ������������");
			System.exit(-2);
			break;
		case VOID_FUNC_CALL_ERR:
			System.out.println("���󣺱��ʽ�е��÷���ֵΪ�յĺ���");
			System.exit(-2);
			break;
		case NO_MAIN_ERR:
			System.out.println("����û��main����");
			System.exit(-2);
			break;
		default:
			System.out.println("����δ����Ĵ���");
			System.exit(-2);
			break;
		}
	}
}
