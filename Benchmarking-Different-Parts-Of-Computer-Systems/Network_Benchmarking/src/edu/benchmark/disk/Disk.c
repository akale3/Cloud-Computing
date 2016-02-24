#include<stdio.h>
#include<stdlib.h>
#include<time.h>
#include<sys/time.h>
#include<string.h>
#include<pthread.h>

#define BYTE 1
#define KILOBYTE 1024
#define MEGABYTE 1024*1024

#define BITERATIONS 100000000
#define KBITERATIONS 100000
#define MBITERATIONS 100

void *sequentialFileWrite(void *byte_ptr);
void *sequentialFileRead(void *byte_ptr);
void *randomFileWrite(void *byte_ptr);
void *randomFileRead(void *byte_ptr);

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

		printf("\n\nExecuting Read Write Operation for %d Threads and for size %d byte.", noOfThread, byteSize);

		//Sequential Write
		printf("\nSequential Write");
		startTime = clock();
		for (i = 0; i < noOfThread; i++) {
			pthread_create(&th[i], NULL, sequentialFileWrite, &byteSize);
			pthread_join(th[i], NULL);
		}
		endTime = clock();
		timeDiff = ((double) (endTime - startTime)) / CLOCKS_PER_SEC;
		latency = (timeDiff * 1000) / (double) ((noOfThread) * iterations);
		printf("\nLatency : %f ms", latency);
		throughput = ((noOfThread * iterations * byteSize)
						/ (double) (timeDiff * MEGABYTE));
		printf("\nThroughtput:%f MB/s", throughput);

		//Sequential Read
		printf("\nSEQUENTIAL Read");
		startTime = clock();
		for (i = 0; i < noOfThread; i++) {
			pthread_create(&th[i], NULL, sequentialFileRead, &byteSize);
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
			pthread_create(&th[i], NULL, randomFileWrite, &byteSize);
			pthread_join(th[i], NULL);
		}
		endTime = clock();
		timeDiff = ((double) (endTime - startTime)) / CLOCKS_PER_SEC;
		latency = (timeDiff * 1000) / (double) ((noOfThread) * iterations);
		printf("\nLatency : %f ms", latency);
		throughput = ((noOfThread * iterations * byteSize)
						/ (double) (timeDiff * MEGABYTE));
		printf("\nThroughtput:%f MB/s", throughput);

		//Random Read
		printf("\nRANDOM Read");
		startTime = clock();
		for (i = 0; i < noOfThread; i++) {
			pthread_create(&th[i], NULL, randomFileRead, &byteSize);
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
void *sequentialFileWrite(void *byte_ptr) {
	int *b_ptr = (int *) byte_ptr;
	FILE *filePointer;
	int i, j;
	int iterations;
	if (*b_ptr == BYTE) {
		iterations = BITERATIONS;
	} else if (*b_ptr == KILOBYTE) {
		iterations = KBITERATIONS;
	} else if (*b_ptr == MEGABYTE) {
		iterations = MBITERATIONS;
	}
	char c[*b_ptr];
	for (i = 0; i < *b_ptr; i++) {
		c[i] = 'a';
	}

	filePointer = fopen("temp.txt", "a");
	for (j = 0; j < iterations; j++) {
		fwrite(c, 1, *b_ptr, filePointer);
	}

	fclose(filePointer);
}

// Read file sequentially
void *sequentialFileRead(void *byte_ptr) {
	FILE *filePointer;
	int *b_ptr = (int *) byte_ptr;
	int i;
	int iterations;
	filePointer = fopen("temp.txt", "r+");
	char buffer[*b_ptr];
	if (*b_ptr == BYTE) {
		iterations = BITERATIONS;
	} else if (*b_ptr == KILOBYTE) {
		iterations = KBITERATIONS;
	} else if (*b_ptr == MEGABYTE) {
		iterations = MBITERATIONS;
	}
	for (i = 0; i < iterations; i++) {
		fread(buffer, *b_ptr, 1, filePointer);
	}
	fclose(filePointer);
}

// Write file Randomly
void *randomFileWrite(void *byte_ptr) {
	FILE *filePointer;
	int *b_ptr = (int *) byte_ptr;
	int i, j;
	int iterations;
	char c[*b_ptr];
	for (j = 0; j < *b_ptr; j++) {
		c[j] = 'a';
	}
	if (*b_ptr == BYTE) {
		iterations = BITERATIONS;
	} else if (*b_ptr == KILOBYTE) {
		iterations = KBITERATIONS;
	} else if (*b_ptr == MEGABYTE) {
		iterations = MBITERATIONS;
	}
	filePointer = fopen("temp.txt", "a");
	for (i = 0; i < iterations; i++) {
		int r = rand() % *b_ptr;
		fseek(filePointer, r, SEEK_SET);
		fwrite(c, 1, *b_ptr, filePointer);
	}

	fclose(filePointer);
}

// Read file Randomly
void *randomFileRead(void *byte_ptr) {
	FILE *filePointer;
	int *b_ptr = (int *) byte_ptr;
	int i;
	int iterations;
	filePointer = fopen("temp.txt", "r+");
	char buffer[*b_ptr];
	if (*b_ptr == BYTE) {
		iterations = BITERATIONS;
	} else if (*b_ptr == KILOBYTE) {
		iterations = KBITERATIONS;
	} else if (*b_ptr == MEGABYTE) {
		iterations = MBITERATIONS;
	}
	for (i = 0; i < iterations; i++) {
		int r = rand() % *b_ptr;
		fseek(filePointer, r, SEEK_SET);
		fread(buffer, *b_ptr, 1, filePointer);
	}
	fclose(filePointer);
}
