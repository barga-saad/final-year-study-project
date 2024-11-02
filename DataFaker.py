from faker import Faker
import random

# Initialisation de Faker
fake = Faker()

# Liste pour stocker les données générées
data = []

# Génération des données
for _ in range(1000):
    IdCustomer = random.randint(1, 10000)
    Nom = fake.last_name()
    Prenom = fake.first_name()
    LegalId = fake.bothify(text='??###??##')
    LegalDocName = random.randint(1, 10000)  # Supposons que cela représente un type de document
    Gender = random.choice(['Male', 'Female'])
    AccountOfficerId = random.randint(1, 1000)
    CustomerStatus = random.randint(1, 10)  # Supposons que cela représente un statut
    Country = fake.country()
    DateOfBirth = fake.date_of_birth(minimum_age=18, maximum_age=90).isoformat()
    Ville = fake.city()
    Adress = fake.address().replace('\n', ', ')  # Remplacer les sauts de ligne par des virgules
    CodeTribunal = fake.bothify(text='???###')
    Mnemonic = fake.lexify(text='??????')  # Générer un mnemonic aléatoire

    # Ajouter les données générées à la liste
    data.append((IdCustomer, Nom, Prenom, LegalId, LegalDocName, Gender, AccountOfficerId, CustomerStatus, Country, DateOfBirth, Ville, Adress, CodeTribunal, Mnemonic))

# Afficher les données générées
for entry in data:
    print(entry)
