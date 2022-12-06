Homework6
===================
## 2018007965 김산

a) Purpose of the Project
----------------
$x' = a_1x + a_2y + a_3$\
$y' = a_4x + a_5y + a_6$

Find the best fit data parameter that fits the given data

b) Implementation
---------------
```c
typedef struct
{
    float* x;
    float* y;
    float* xp;
    float* yp;
} Data;

void readData(FILE* file, Data* data){
    for (int i = 0; i < 77; i++) {
		fscanf(file, "%f %f %f %f", &data->x[i], &data->y[i], &data->xp[i], &data->yp[i]);
	}
}
```
fitdata 파일이 각각 77줄이므로 `Data` 구조체를 만들고 `readData()`함수를 호출하여 파일을 한 줄 씩 읽어와서 x,y,xp,yp에 값을 넣어준다.

```c
void fitData(float** matr, Data* data, int N, int M, int K)
{
	float** A = matrix(1, M, 1, M);

	for (int i = 1; i <= M; i++) {
		for (int j = 1; j <= M; j++) {
			A[i][j] = 0;
			if (j <= 2) {
				A[i][j] = 0;
			}
		}
	}

	for (int i = 0; i < N; i++) {
		A[1][1] += data->x[i] * data->x[i];
		A[1][2] += data->x[i] * data->y[i];
		A[1][3] += data->x[i];
		A[2][2] += data->y[i] * data->y[i];
		A[2][3] += data->y[i];
		matr[1][1] += data->x[i] * data->xp[i];
		matr[1][2] += data->x[i] * data->yp[i];
		matr[2][1] += data->xp[i] * data->y[i];
		matr[2][2] += data->y[i] * data->yp[i];
		matr[3][1] += data->xp[i];
		matr[3][2] += data->yp[i];
	}
	A[2][1] = A[1][2];
	A[3][1] = A[1][3];
	A[3][2] = A[2][3];
	A[3][3] = N;

	gaussj(A, M, matr, K);

}
```

$$\begin{bmatrix}
\sum x_i x_i & \sum x_i y_i & \sum x_i \\
\sum x_i y_i & \sum y_i y_i & \sum y_i \\
\sum x_i & \sum y_i & \sum 1
\end{bmatrix}
\begin{bmatrix}
a_1 & a_4 \\
a_2 & a_5 \\
a_3 & a_6
\end{bmatrix} = 
\begin{bmatrix}
\sum x_i' x_i & \sum x_i y_i' \\
\sum x_i' y_i & \sum y_i' y_i \\
\sum x_i' & \sum y_i'
\end{bmatrix}
$$

위와 같은 행렬의 결과값을 구현하기 위해 `fitData()`를 만들어서 `matr`행렬을 통해 구현을 하였고, 이를 마지막에 `gaussj`를 활용하면서 연산을 하였다.

그리고 `main`함수에서 구조체를 initialize해주고 정의한 함수를 활용하여 best fit data를 구한다.

c) How to build
--------------------------------------
```c
make
./main
```
