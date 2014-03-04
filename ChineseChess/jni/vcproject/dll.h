#ifdef DLL_EXPORTS
#define DLL_API __declspec(dllexport)
#else
#define DLL_API __declspec(dllimport)
#endif

// 此类是从 dll.dll 导出的
class DLL_API Cdll {
public:
	Cdll(void);
	// TODO: 在此添加您的方法。
};

extern DLL_API int ndll;

DLL_API int fndll(void);
