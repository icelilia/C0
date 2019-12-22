package error;

public enum ErrEnum {

	PARA_ERR, // ��������
	INPUT_FILE_ERR, // �����ļ�������
	OUTPUT_FILE_ERR, // �޷���������ļ�

	INPUT_ERR, // ���뺬�зǷ��ַ�����ʮ������������������ʽ����
	OUTPUT_ERR, // ��������ļ�д�����

	INT_OF_ERR, // 32λ�������������

	SP_ERR, // ������󣬽�������

	EOF_ERR, // ������;����EOF

	ID_ERR, // ID����ȱ�ٱ�ʶ��

	UK_TYPE_ERR, // ��֧�ֵ���������

	CONST_DECL_ERR, // ��������ʱ�﷨����

	CONST_INIT_ERR, // ����δ����ʾ�س�ʼ��
	
	CONST_AS_ERR, // �����޷����ٴθ�ֵ

	ID_REDECL_ERR, // �����ض���

	VAR_DECL_ERR, // ��������ʱ�﷨����

	VAR_REDECL_ERR, // �����ض���

	SEM_ERR, // ȱ�ٷֺ�

	EXP_ERR, // ���ʽ�д��ڴ���

	ID_UNDECL_ERR, // ��ʶ��δ����

	VAR_UNINIT_ERR, // ʹ����δ��ʼ���ı���

	FUNC_REDECL_ERR, // �����ض���

	LSB_ERR, // ȱ����-С����

	RSB_ERR, // ȱ����-С����

	FUNC_PARA_DECL_ERR, // ������������ʱ�﷨����

	LLB_ERR, // ȱ����-������

	RLB_ERR, // ȱ����-������

	FUNC_STATMENT_ERR, // ��������﷨����
	
	FUNC_PARA_ERR, // �������ô���
	
	VOID_FUNC_CALL_ERR, // ���ʽ�е��÷���ֵΪ�յĺ���

	ErrNoBegin, ErrNoEnd, ErrNeedIdentifier, ErrConstantNeedValue, ErrNoSemicolon, ErrInvalidVariableDeclaration,
	ErrIncompleteExpression, ErrNotDeclared, ErrAssignToConstant, ErrDuplicateDeclaration, ErrNotInitialized,
	ErrInvalidAssignment, ErrInvalidPrint
}
