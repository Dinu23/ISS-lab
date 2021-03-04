# ISS-lab
5. URMARIRE BUG-URI O firma producatoare de software pune la dispozitia programatorilor si verificatorilor sai un sistem prin care acestia pot sa comunice electronic. Astfel, fiecare dintre angajatii mentionati are la dispoziție un terminal prin care: 
1.verificatorul  poate  înregistra  un  bug,  dându-i  o  denumire  si  o  descriere;  imediat  dupa înregistrarea bug-ului, toti  programatorii vad  lista bug-urilor  actualizata cu obiectul nou introdus; 
2.programatorul vizualizeaza lista bug-urilor; de asemenea, programatorul poate selecta un bug pe care doreste sa incerce sa il rezolve (bug -ul trece din starea new/not resolved  -> on-going(nu mai poate fi selectat de alt programator))
	  2.1 acesta poate sa adauge materiale ajutatoare si detalii despre bug daca nu resueste sa il rezolve  (bug -ul trece din starea on-going ->not resolved)
	  2.2 acesta poate alege sa fie trimis bug-ul catre testare( on-going-> ready for testing)

3.verificatorul va testa bug-ul care are starea ready for testing
	3.1 daca bug-ul este rezolvat acesta ii schimba starea in done
	3.2 daca bug-ul nu este rezolvat acesta este trimis din nou in starea de not-resolved si pioate fi ales din nou de un programator sa fie rezolvat

In functie de dificultatea bug-ului acesta are un rating (de la usor -> nu se poate rezolva) in functie de numarul de retrimiteri a bug-ului in sistem dupa testare
