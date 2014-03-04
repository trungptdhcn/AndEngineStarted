chess: ../vcproject/test.cpp ../engine.cpp ../engine.h ../bkdata.cpp ../zobrist.h
	g++ -g -Wall -DGCC_DEBUG -o chess ../vcproject/test.cpp ../engine.cpp ../bkdata.cpp

clean: $(cleanpo_dirs)
	rm -f *.o chess
	
.PHONY: clean