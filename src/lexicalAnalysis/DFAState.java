package lexicalAnalysis;

// ����C0��DFA״̬��״̬
enum DFAState {
	INIT_STATE, // ��ʼ
	DEC_INT_STATE, // ʮ��������
	HEX_INT_STATE, // ʮ����������
	ID_STATE, // ��ʶ��

	PLUS_STATE, // +
	MINUS_STATE, // -
	MUL_STATE, // *
	DIV_STATE, // /
	E_STATE, // =

	L_STATE, // <
	LE_STATE, // <=
	G_STATE, // >
	GE_STATE, // >=
	UE_STATE, // !=
	EE_STATE, // ==

	COMMA_STATE, // ,
	SEM_STATE, // ;
	LSB_STATE, // (
	RSB_STATE, // )
	LMB_STATE, // [
	RMB_STATE, // ]
	LLB_STATE, // {
	RLB_STATE, // }
};
