#include<stdio.h>
#include<stdlib.h>
#include<time.h>
#include<sys/time.h>
#include<pthread.h>
#include<string.h>

#define ITERATIONS 10000000

void *threadExecutionForIops(void *itrPtr);
void *threadExecutionForFlops(void *itrPtr);
void countIops(int numberOfThreads);
void countFlops(int numberOfThreads);

int main() {
	int choice;
	while (1) {
		printf("\nEnter your choice.\n1. To execute one thread.\n2. To execute Two concurrent threads.\n3. To execute four concurrent threads.\n4. Exit.\n");
		scanf("%d", &choice);
		switch(choice){
			case 1:
				countFlops(1);
				countIops(1);
				break;
			case 2:
				countFlops(2);
				countIops(2);
				break;
			case 3:
				countFlops(4);
				countIops(4);
				break;
			case 4:
				exit(0);
			default:
				printf("\n Please enter valid choice.");
				break;
		}
	}
	return 0;
}

// function to measure Giga IOPS
void countIops(int numberOfThreads) {
	clock_t start;
	clock_t end;
	double cpuTimeUsed;
	pthread_t th[5]; // array of threads
	int i;
	long iterations = ITERATIONS / numberOfThreads;

	printf("\nProgram to find IOPS for %d concurrent threads", numberOfThreads);
	// Executing concurrent threads performing same IOPS operations.
	start = clock();
	for (i = 0; i < numberOfThreads; i++) {
		pthread_create(&th[i], NULL, threadExecutionForIops, &iterations);
		pthread_join(th[i], NULL);
	}
	end = clock();
	cpuTimeUsed = ((double) (end - start)) / CLOCKS_PER_SEC;
	printf("\nTotal Time Required to perform all operations is : %f ms\n", cpuTimeUsed);
	double Iops = (ITERATIONS * 20) / (double) (cpuTimeUsed);
	// Calculate Giga Flops Formula: Flops * 10^(-9).
	double gIops = (double) Iops / 1000000000;
	printf("Giga IOPS : %f\n", gIops);
}

// thread executing operations to calculate Iops
void *threadExecutionForIops(void *itrPtr) {
	int i;
	int a = 5;
	long *iterations = (long*) itrPtr;
	for (i = 0; i < *iterations; i++) {
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		//a = a + a;
	}
	return NULL;
}

// function to measure Giga FLOPS
void countFlops(int numberOfThreads) {
	clock_t start;
	clock_t end;
	double cpuTimeUsed;
	pthread_t th[5]; // array of threads
	int i;
	long iterations = ITERATIONS / numberOfThreads;

	printf("\nProgram to find FLOPS for %d concurrent threads", numberOfThreads);
	// Executing concurrent threads performing same FLOPS operations.
	start = clock();
	for (i = 0; i < numberOfThreads; i++) {
		pthread_create(&th[i], NULL, threadExecutionForFlops, &iterations);
		pthread_join(th[i], NULL);
	}
	end = clock();
	cpuTimeUsed = ((double) (end - start)) / CLOCKS_PER_SEC;

	printf("\nTotal Time Required to perform all operations is : %f ms\n", cpuTimeUsed);
	double Flops = (ITERATIONS * 20) / (double) (cpuTimeUsed);

	// Calculate Giga Flops Formula: Flops * 10^(-9).
	double gFlops = (double) Flops / 1000000000;
	printf("Giga FLOPS : %f\n", gFlops);
}

// thread executing operations to calculate Flops
void *threadExecutionForFlops(void *itrPtr) {
	int i;
	double a = 5.2;
	//  convert string argument to long
	long *iterations = (long*) itrPtr;
	for (i = 0; i < *iterations; i++) {
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
		a = a + i;
	}
	return NULL;
}

