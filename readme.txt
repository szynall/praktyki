Aplikacja posiada 2 funkcje, które u¿ywa³em w trakcie pracy, a nie by³o sensu chyba ich usuwaæ.
Pierwsz¹ z nich jest exportowanie siatki w formacie .obj.
Drug¹ z nich jest skalowanie zmiennej Z. Zeskalowane np do 10% wygl¹da o wiele lepiej i tak te¿ ³atwiej mi siê usuwa³o b³êdy.

Aplikacja przeszukuje plik XML, naprawia b³êdne pozycje (za b³êdne pozycje uznaje, te z ujemn¹ wartoœci¹ Z).
Nastêpnie stosujê bardzo prosty algorytm podzia³u na trójk¹ty. 
Po konsultacjach z koleg¹ grafikiem odpuœci³em sprawdzanie czy po podziale nie utworzy³y siê nieefektywne (zgodnie z moim podejœciem) podzia³y. Planowa³em zrobiæ to jako osobn¹ opcjê i implementacja by³a gotowa, jednak uzna³em j¹ bardziej zbêdna, ani¿eli pozosta³e funkcje.

Do zapisu do bazy korzystam z pliku tymczasowego. Wtedy insertowanie jest o wiele szybsze.

Niestety z powodu nat³oku zajêæ na uczelni nie uda³o mi siê zrobiæ po³¹czenia z zastosowaniem ORM. Zgodnie z poleceniem w bazie umieœci³em informacjê o trójk¹tach. Uzna³em, ¿e wiedza o punktach nie jest ju¿ potrzebna, a tworzenie innej tabeli i póŸniejsze ewentualne JOIN'owanie raczej nie by³oby zbyt efektywne, st¹d wybór jednej tabeli. Przyznajê ta czêœæ nie jest zbyt chwalebna. W projekcie s¹ pozosta³oœci z prób zaprzêgniêcia Hibernate.