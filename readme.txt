Aplikacja posiada 2 funkcje, kt�re u�ywa�em w trakcie pracy, a nie by�o sensu chyba ich usuwa�.
Pierwsz� z nich jest exportowanie siatki w formacie .obj.
Drug� z nich jest skalowanie zmiennej Z. Zeskalowane np do 10% wygl�da o wiele lepiej i tak te� �atwiej mi si� usuwa�o b��dy.

Aplikacja przeszukuje plik XML, naprawia b��dne pozycje (za b��dne pozycje uznaje, te z ujemn� warto�ci� Z).
Nast�pnie stosuj� bardzo prosty algorytm podzia�u na tr�jk�ty. 
Po konsultacjach z koleg� grafikiem odpu�ci�em sprawdzanie czy po podziale nie utworzy�y si� nieefektywne (zgodnie z moim podej�ciem) podzia�y. Planowa�em zrobi� to jako osobn� opcj� i implementacja by�a gotowa, jednak uzna�em j� bardziej zb�dna, ani�eli pozosta�e funkcje.

Do zapisu do bazy korzystam z pliku tymczasowego. Wtedy insertowanie jest o wiele szybsze.

Niestety z powodu nat�oku zaj�� na uczelni nie uda�o mi si� zrobi� po��czenia z zastosowaniem ORM. Zgodnie z poleceniem w bazie umie�ci�em informacj� o tr�jk�tach. Uzna�em, �e wiedza o punktach nie jest ju� potrzebna, a tworzenie innej tabeli i p�niejsze ewentualne JOIN'owanie raczej nie by�oby zbyt efektywne, st�d wyb�r jednej tabeli. Przyznaj� ta cz�� nie jest zbyt chwalebna. W projekcie s� pozosta�o�ci z pr�b zaprz�gni�cia Hibernate.