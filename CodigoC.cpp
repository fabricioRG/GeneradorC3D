#include <iostream> 
#include <string>
#define N 256 
using namespace std;
int main(){
double d1;
double d2;
double res;
double po;
int op;
float float1;
long long1;
long long2;
int int1;
unsigned char byte1;
char char1;
bool boolean1;
bool boolean2;
string string1;
string matrix[N];
int i1;
int i2;
double t14;
double t14$00num1$2120;
double t14$00num2$2133;
double t14$00res$2212;
double t14$00t1;
double t15;
double t15$00base$2824;
int t15$00potencia$2834;
double t15$00res$2912;
int t15$00i$309;
int t15$00t3;
double t15$00t2;
double t16;
int t16$00num$3921;
int t16$00div$3930;
int t16$00res$409;
int t16$00t4;
int t17$00i$499;
int t17$00j$4915;
string t17$00val$5012;
int t17$00t5;
int t17$00t6;
int t17$00t7;
int t17$00t8;
int t18$00i$659;
int t18$00j$6515;
string t18$00val$6612;
string t18$00t11;
int t18$00t9;
int t18$00t10;
int t18$00t12;
int t18$00t13;
d1 = 0;
d2 = 3;
et36:
cout << "opciones:"<< endl;
cout << "1. suma"<< endl;
cout << "2. potencia"<< endl;
cout << "3. division"<< endl;
cout << "4. llenar matrix"<< endl;
cout << "5. ver matrix"<< endl;
cout << "6. terminar"<< endl;
cout << "Opcion?"<< endl;
cin >>  op;
et25:
if (op == 1) goto et26;
goto et27;
et26:
cout << "num1:"<< endl;
cin >>  d1;
cout << "num2:"<< endl;
cin >>  d2;
t14$00num1$2120 = d1;
t14$00num2$2133 = d2;
t14$00res$2212 = 0;
t14$00t1 = t14$00num1$2120 + t14$00num2$2133;
t14$00res$2212 = t14$00t1;
t14 = t14$00res$2212;
res = t14;
cout << "resultado suma:"<< endl;
cout << res<< endl;
goto  et36;
et27:
if (op == 2) goto et28;
goto et29;
et28:
cout << "base:"<< endl;
cin >>  d1;
cout << "potencia:"<< endl;
cin >>  int1;
t15$00base$2824 = d1;
t15$00potencia$2834 = int1;
t15$00res$2912 = 1;
t15$00i$309 = 0;
t15$00i$309 = 0;
t15$00et1:
if (t15$00i$309 < t15$00potencia$2834) goto t15$00et2;
goto t15$00et3;
t15$00et2:
t15$00t3 = t15$00i$309 + 1;
t15$00t2 = t15$00res$2912 * t15$00base$2824;
t15$00res$2912 = t15$00t2;
goto t15$00et1;
t15$00et3:
t15 = t15$00res$2912;
res = t15;
cout << "resultado potencia:"<< endl;
cout << res<< endl;
goto  et36;
et29:
if (op == 3) goto et30;
goto et31;
et30:
cout << "numerador:"<< endl;
cin >>  i1;
cout << "divisor:"<< endl;
cin >>  i2;
t16$00num$3921 = i1;
t16$00div$3930 = i2;
t16$00res$409 = 0;
t16$00et4:
if (t16$00div$3930 == 0) goto t16$00et5;
goto t16$00et6;
t16$00et5:
cout << "NO SE PUEDE DIVIDIR ENTRE CERO"<< endl;
goto  t16$00et7;
t16$00et6:
t16$00t4 = t16$00num$3921 / t16$00div$3930;
t16$00res$409 = t16$00t4;
goto  t16$00et7;
t16$00et7:
t16 = t16$00res$409;
res = t16;
cout << "resultado division:"<< endl;
cout << res<< endl;
goto  et36;
et31:
if (op == 4) goto et32;
goto et33;
et32:
cout << "Llenando matrix:"<< endl;
t17$00i$499 = 0;
t17$00j$4915 = 0;
t17$00et8:
if (t17$00i$499 < 4) goto t17$00et9;
goto t17$00et10;
t17$00et9:
t17$00j$4915 = 0;
t17$00et11:
if (t17$00j$4915 < 3) goto t17$00et12;
goto t17$00et13;
t17$00et12:
cout << "VALOR "<< endl;
cout << t17$00i$499<< endl;
cout << ", "<< endl;
cout << t17$00j$4915<< endl;
cout << ":"<< endl;
cin >>  t17$00val$5012;
t17$00t5 = t17$00i$499 * 3;
t17$00t6 = t17$00t5 + t17$00j$4915;
matrix [t17$00t6] = t17$00val$5012;
t17$00t7 = t17$00j$4915 + 1;
t17$00j$4915 = t17$00t7;
goto t17$00et11;
t17$00et13:
t17$00t8 = t17$00i$499 + 1;
t17$00i$499 = t17$00t8;
goto t17$00et8;
t17$00et10:
cout << "matrix llena"<< endl;
goto  et36;
et33:
if (op == 5) goto et34;
goto et35;
et34:
cout << "viendo matrix:"<< endl;
t18$00i$659 = 0;
t18$00j$6515 = 0;
t18$00et16:
if (t18$00i$659 < 4) goto t18$00et17;
goto t18$00et18;
t18$00et17:
t18$00j$6515 = 0;
t18$00et19:
if (t18$00j$6515 < 3) goto t18$00et20;
goto t18$00et21;
t18$00et20:
t18$00t9 = t18$00i$659 * 3;
t18$00t10 = t18$00t9 + t18$00j$6515;
t18$00t11 = matrix [t18$00t10];
t18$00val$6612 = t18$00t11;
cout << "VALOR"<< endl;
cout << t18$00i$659<< endl;
cout << ", "<< endl;
cout << t18$00j$6515<< endl;
cout << ": "<< endl;
cout << t18$00val$6612<< endl;
t18$00t12 = t18$00j$6515 + 1;
t18$00j$6515 = t18$00t12;
goto t18$00et19;
t18$00et21:
t18$00t13 = t18$00i$659 + 1;
t18$00i$659 = t18$00t13;
goto t18$00et16;
t18$00et18:
cout << "fin impresion"<< endl;
goto  et35;
et35:
cout << "SALIENDO"<< endl;
goto  et39;
et39:
if (op != 6) goto et37;
goto et38;
et37:
goto et36;
et38:
cout << "SALIENDO"<< endl;

return 0;
}