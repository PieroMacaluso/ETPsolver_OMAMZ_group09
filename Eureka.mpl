	
INDEX
	t:=1..6;	!slot temporali
	e1:=1..4;	!esami
	e2:=e1;
	k:=1..5;	!indice delle booleane di penalita
DATA
	n[e1,e2]=DATAFILE("tabella.dat") !grafo dei conflitt	
	tmax=6;
	S=8;
	M=2*tmax;
VARIABLES
	cal[t,e1];	!il "calendario"
	i[e1,e2] WHERE (e1>e2) ;	!la distanza temporale
	y[e1,e2,k] WHERE (e1>e2);	!booleano associata alla penalita
	w[e1,e2,k] WHERE (e1>e2);
	z[e1,e2,k] WHERE (e1>e2);
	B[e1,e2] WHERE (e1>e2);	!booleano indica il segno della distanza
	tau[e1,e2] WHERE (e1>e2);	!indica quando c'e conflitto o no

MODEL	MIN SUM(e1,e2: n[e1,e2] / S * (16*y[e1,e2,1] + 8*y[e1,e2,2] + 4*y[e1,e2,3] + 2*y[e1,e2,4] + y[e1,e2,5]  ) ) ;


SUBJECT TO

	ApplyExam[e1]:	SUM(t: cal[t,e1]) = 1;
	
	DistanceTime1[e1,e2<e1]:	i[e1,e2] > SUM(t: t * cal[t,e1]) - SUM(t: t * cal[t,e2] );
	DistanceTime2[e1,e2<e1]:	i[e1,e2] > SUM(t: t * cal[t,e2]) - SUM(t: t * cal[t,e1] );
	
	DistanceTime3[e1,e2<e1]:	i[e1,e2] < SUM(t: t * cal[t,e1]) - SUM(t: t * cal[t,e2] ) + B*M;
	DistanceTime4[e1,e2<e1]:	i[e1,e2] < SUM(t: t * cal[t,e2]) - SUM(t: t * cal[t,e1] )+ M*(1-B);
	DistanceTime5[e1,e2<e1]:	 M*B > SUM(t: t * cal[t,e1]) - SUM(t: t * cal[t,e2] );
	DistanceTime6[e1,e2<e1]:	M*(1-B) > SUM(t: t * cal[t,e1]) - SUM(t: t * cal[t,e2] );

	ConflictAvoid1[e1,e2<e1]:	n[e1,e2] < M * tau[e1,e2];
	ConflictAvoid2[e1,e2<e1]:	n[e1,e2] > tau[e1,e2];
	ConflictAvoid3[t,e1,e2<e1]:	cal[t,e1] + cal[t,e2] < 1 + (1 - tau[e1,e2]);

	PenalityFunction1[e1,e2<e1,k]: i[e1,e2] > k * w[e1,e2,k] ;
	PenalityFunction2[e1,e2<e1,k]: i[e1,e2] < (k - 1) + M * w[e1,e2,k];
	
	PenalityFunction3[e1,e2<e1,k]: i[e1,e2] >  k * (1 - z[e1,e2,k]) + 1 - z[e1,e2,k];
	PenalityFunction4[e1,e2<e1,k]: i[e1,e2] < k + M * (1 - z[e1,e2,k]);
	
	PenalityFunction5[e1,e2<e1,k]: w[e1,e2,k] + z[e1,e2,k] < y[e1,e2,k] + 1;
	PenalityFunction6[e1,e2<e1,k]: w[e1,e2,k] + z[e1,e2,k] > 2 * y[e1,e2,k];

	
BINARY
	cal[t,e1];	!il "calendario"
	y[e1,e2,k];	!booleano associata alla penalita
	w[e1,e2,k];
	z[e1,e2,k];
	B[e1,e2];	!booleano indica il segno della distanza
	tau[e1,e2];	!indica quando c'e conflitto o no

INTEGER
	i[e1,e2];	!la distanza temporale

BOUNDS

	LastHope[e1,e2>=e1,k]: y[e1,e2,k]=100;