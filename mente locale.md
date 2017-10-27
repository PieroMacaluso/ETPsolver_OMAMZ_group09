# Mente locale

## Funzione obiettivo

    MIN 1/|S|·sommatoria_e,e'((16·y[e,e'] + ... + 1·y[e,e'])·n[e,e'])

## Vincoli

1. Distanza, 

### Distanza

`i` e' la distanza temporale

     y[i,e,e']

### Azzerare/accendere penalita' di distanza se (non) c'e' conflitto

Booleano `x`, 0 se conflitto e 1 se no

	x[e,e'] >= y[e,e']

Poi c'e' da considerare

    x legato in qualche modo a n[e,e']

Le 2 parti vanno unite, in modo da relazionare `y` con `n`

### Tabella degli slot temporali

Matrice con le distanze degli esami

    i[e,e'] = |sommatoria_i(e_i·t_i·delta-t_i)-sommatoria_i(e'_i·t_i·delta-t_i)|

con

    delta-t[t_i] = 1,2,3,4,5,6+

# Mente locale/bis

Alternativa.

1. Tabella C di `e` e `e'` con 0 se compatibili, 1 se incompatibili (da cui si ricava il vincolo duro© della non sovrapposizione)
2. Tabella tridimensionale con `e` e `e'` e la distanza (0,1,2,3,4,5,6+), valori:
     
     0 SE 6+
     formula·C[e,e']

Poi e' tutto legato insieme in modi strani che boh.
