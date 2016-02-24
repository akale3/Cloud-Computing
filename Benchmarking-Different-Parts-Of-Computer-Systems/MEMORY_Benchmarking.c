#include<stdio.h>
#include<stdlib.h>
#include<time.h>
#include<sys/time.h>
#include<string.h>
#include<pthread.h>

#define BYTE 1
#define KILOBYTE 1024
#define MEGABYTE 1024*1024
#define MEMORYSIZE 900 * 1024 * 1024

#define BITERATIONS 10000000
#define KBITERATIONS 10000
#define MBITERATIONS 10

void *sequentialReadWrite(void *byte_ptr);
void *randomReadWrite(void *byte_ptr);
char* srcPtr;
char* destPtr;
char* srcPtrRandom;
char* destPtrRandom;

void main() {
	clock_t startTime;
	clock_t endTime;
	double timeDiff;
	double latency;
	double throughput;

	int i;
	int j;
	int byteSize;
	int iterations;
	int choice;
	int noOfThread;
	pthread_t th[4]; // array of threads

	srcPtr = (char*) malloc(MEMORYSIZE);
	destPtr = (char*) malloc(MEMORYSIZE);
	srcPtrRandom = (char*) malloc(MEMORYSIZE);
	destPtrRandom = (char*) malloc(MEMORYSIZE);

	int threadChoice[6] = { 1, 2, 1, 2, 1, 2 };
	int SizeChoice[6] = { 1, 1, 2, 2, 3, 3 };

	for (j = 0; j < 6; j++) {

		choice = SizeChoice[j];
		noOfThread = threadChoice[j];

		if (choice == 1) {
			byteSize = BYTE;
			iterations = BITERATIONS;
		} else if (choice == 2) {
			byteSize = KILOBYTE;
			iterations = KBITERATIONS;
		} else if (choice == 3) {
			byteSize = MEGABYTE;
			iterations = MBITERATIONS;
		}

		printf("\n\nExecuting Memory Read Write Operation for %d Thread and of size %d byte.", noOfThread, byteSize);

		//Sequential Write
		printf("\nSequential Read Write");
		startTime = clock();
		for (i = 0; i < noOfThread; i++) {
			pthread_create(&th[i], NULL, sequentialReadWrite, &byteSize);
			pthread_join(th[i], NULL);
		}
		endTime = clock();
		timeDiff = ((double) (endTime - startTime)) / CLOCKS_PER_SEC;
		latency = (timeDiff * 1000) / (double) ((noOfThread) * iterations);
		printf("\nLatency : %f ms", latency);
		throughput = ((noOfThread * iterations * byteSize)
						/ (double) (timeDiff * MEGABYTE));
		printf("\nThroughtput:%f MB/s", throughput);


		//Random Write
		printf("\nRANDOM Write");
		startTime = clock();
		for (i = 0; i < noOfThread; i++) {
			pthread_create(&th[i], NULL, randomReadWrite, &byteSize);
			pthread_join(th[i], NULL);
		}
		endTime = clock();
		timeDiff = ((double) (endTime - startTime)) / CLOCKS_PER_SEC;
		latency = (timeDiff * 1000) / (double) ((noOfThread) * iterations);
		printf("\nLatency : %f ms", latency);
		throughput = ((noOfThread * iterations * byteSize)
								/ (double) (timeDiff * MEGABYTE));
		printf("\nThroughtput:%f MB/s", throughput);
	};

}

// write file sequentially
void *sequentialReadWrite(void *byte_ptr) {
	int *b_ptr = (int *) byte_ptr;
	int j;
	int iterations;
	if (*b_ptr == BYTE) {
		iterations = BITERATIONS;
	} else if (*b_ptr == KILOBYTE) {
		iterations = KBITERATIONS;
	} else if (*b_ptr == MEGABYTE) {
		iterations = MBITERATIONS;
	}

	for (j = 0; j < iterations; j++) {
		memcpy(destPtr, srcPtr, *b_ptr);
		destPtr = destPtr + 1;
		srcPtr = srcPtr + 1;
	}

}

// Write file Randomly
void *randomReadWrite(void *byte_ptr) {
	int *b_ptr = (int *) byte_ptr;
	int i;
	int iterations;
	if (*b_ptr == BYTE) {
		iterations = BITERATIONS;
	} else if (*b_ptr == KILOBYTE) {
		iterations = KBITERATIONS;
	} else if (*b_ptr == MEGABYTE) {
		iterations = MBITERATIONS;
	}
	for (i = 0; i < 10; i++) {
		int r = rand() % (MEMORYSIZE - (*b_ptr + 1));
		memcpy(&destPtrRandom[r], &srcPtrRandom[r], *b_ptr);
	}
}
