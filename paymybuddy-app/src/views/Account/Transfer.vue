<template>
  <div class="page-wrapper">
    <div class="transfer-container">

      <p class="balance"><strong>Solde :</strong> {{ balance }} €</p>

      <div class="add-funds-container">
        <p class="addFunds">Alimenter mon compte</p>
        <button @click="addFunds" class="add-funds-button" aria-labelledby="addFundsLabel">+50€</button>
      </div>

      <form @submit.prevent="sendMoney" class="transfer-form">

        <label for="friend" class="sr-only">Sélectionner une relation</label>
        <select id="friend" v-model="selectedFriendEmail" required>
          <option disabled value="">Sélectionner une relation</option>
          <option v-for="friend in friends" :key="friend.username" :value="friend.username">
            {{ friend.username }}
          </option>
        </select>

        <label for="description" class="sr-only">Description</label>
        <input id="description" v-model="description" placeholder="Description" required />

        <label for="amount" class="sr-only">Montant en Euros</label>
        <input id="amount" v-model.number="amount" type="number" min="0.01" step="0.01" placeholder="0€"
          aria-describedby="amount-info" />

        <button class="pay-button" type="submit">Payer</button>
      </form>

      <p class="message" v-if="message" :class="{ error: isError }" role="alert">{{ message }}</p>


      <div class="transaction-container">
        <h2>Mes transactions</h2>

        <table v-if="transactions.length">
          <thead>
            <tr>
              <th scope="col">Relation</th>
              <th scope="col">Description</th>
              <th scope="col">Montant</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="txn in transactions" :key="txn.id">
              <td>{{ txn.receiverUsername }}</td>
              <td>{{ txn.description }}</td>
              <td>{{ txn.amount }}</td>
            </tr>
          </tbody>
        </table>
        <p v-else>Aucune transaction pour l’instant.</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: "transferPage",
  data() {
    return {
      balance: 0,
      friends: [],
      selectedFriendEmail: "",
      description: "",
      amount: null,
      message: "",
      isError: false,
      transactions: [],
    };
  },
  created() {
    this.fetchBalance();
    this.fetchFriends();
    this.fetchTransactions();
  },
  methods: {

    fetchTransactions() {
      axios.get("http://localhost:8081/api/transactions/", {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        },
        withCredentials: true
      })
        .then(res => {
          console.log(res.data);
          this.transactions = res.data;
        });
    },
    fetchBalance() {

      axios.get('http://localhost:8081/api/users/account',
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          },
          withCredentials: true
        }
      )
        .then(res => {
          this.balance = res.data;
        })
        .catch(() => {
          this.message = "Erreur lors de la récupération du solde";
          this.isError = true;
        });
    },
    fetchFriends() {
      axios.get('http://localhost:8081/api/users/getFriends', {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        },
        withCredentials: true
      })
        .then(res => {
          this.friends = res.data;
        })
        .catch(() => {
          this.message = "Erreur lors de la récupération des relations";
          this.isError = true;
        });
    },
    sendMoney() {
      this.message = "";
      this.isError = false;

      if (!this.selectedFriendEmail) {
        this.message = "Veuillez sélectionner une relation";
        this.isError = true;
        return;
      }
      if (!this.amount || this.amount <= 0) {
        this.message = "Veuillez entrer un montant valide";
        this.isError = true;
        return;
      }
      if (!this.description.trim()) {
        this.message = "Veuillez saisir une description";
        this.isError = true;
        return;
      }

      const payload = {

        description: this.description,
        amount: this.amount,
        senderEmail: localStorage.getItem("email"),
        receiverUsername: this.selectedFriendEmail,
      };

      console.log("payload : ", payload);
      axios.post('http://localhost:8081/api/transactions/transaction', payload,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          },
          withCredentials: true
        })
        .then(() => {
          this.message = "Transfert effecuté avec succès";
          this.fetchTransactions
          this.isError = false;

          this.fetchBalance();
          this.fetchTransactions();

          this.selectedFriendEmail = "";
          this.amount = null;
          this.description = "";
        })
        .catch((error) => {
          this.message = error.response?.data?.error || "Une erreur est survenue lors de la transaction";
          this.isError = true;
        });
    },
    addFunds() {
      axios.post('http://localhost:8081/api/users/account/recharge', {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        },
        withCredentials: true
      })
        .then(res => {
          console.log(res.data);
          this.fetchBalance();
          this.message = "50€ ont bien été ajoutés à votre compte";
          this.isError = false;
        })
        .catch(() => {
          this.message = "Erreur lors de l'alimentation de votre compte";
          this.isError = true;
        });
    },
  }
};
</script>

<style>
.page-wrapper {
  margin-top: 100px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  /* prend toute la hauteur de la fenêtre */
}

.transfer-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.balance {
  width: 80%;
  margin: auto;
  margin-bottom: 20px;
}

.transfer-form {
  width: 80%;
  height: 15vh;
  margin: auto;
  margin-bottom: 80px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

#friend {
  margin: 10px;
  padding: 10px;
  border: 1px solid #ccc;
  width: 30%;
}

#description {
  border: 1px solid #ccc;
  width: 30%;
}

#amount {
  border: 1px solid #ccc;
  width: 10%;
}

.pay-button {
  font-size: 15px;
}

.add-funds-container {
  width: 80%;
  margin: auto;
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
}

.add-funds-container button {
  font-size: 12px;
  background-color: #F69F1D;
  border: 1px solid #F69F1D;
}

.transaction-container {
  border: 1px solid #ccc;
  width: 80%;
  margin: auto;
  padding: 20px;
  margin-bottom: 30px;

}

table {
  width: 100%;
  margin: auto;
  padding: 20px;
}

th,
td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ccc;
}

thead h2 {
  margin-bottom: 10px;
}

thead th {
  font-weight: bold;
}

.message {
  color: red;
  width: 80%;
  margin: auto;
  margin-bottom: 20px;
}

/* Lecteur d'écran */ 
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}
</style>
