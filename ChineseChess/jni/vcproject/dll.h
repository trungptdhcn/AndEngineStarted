#ifdef DLL_EXPORTS
#define DLL_API __declspec(dllexport)
#else
#define DLL_API __declspec(dllimport)
#endif

// �����Ǵ� dll.dll ������
class DLL_API Cdll {
public:
	Cdll(void);
	// TODO: �ڴ�������ķ�����
};

extern DLL_API int ndll;

DLL_API int fndll(void);
