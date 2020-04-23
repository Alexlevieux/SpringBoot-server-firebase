
 firebase = require('firebase/app');
 require('firebase/auth');
var fetch = require('./fetchFirebase.js')

if (firebase) {
  console.log('firebase initialised');
  var firebaseConfig = {
    apiKey: "AIzaSyAGi9vDZlmx15Y3IiYQkCoupzhHFyHIRCU",
    authDomain: "bookboa rd-list.firebaseapp.com",
    databaseURL: "https://bookboard-list.firebaseio.com",
    projectId: "bookboard-list",
    storageBucket: "bookboard-list.appspot.com",
    messagingSenderId: "604945667990",
    appId: "1:604945667990:web:02bce2abd7fecf279d6fb7",
    measurementId: "G-67ZTNZ3Y0S"
  }; // Initialize Firebase

  firebase.initializeApp(firebaseConfig);
}
 initApp = function() {
        firebase.auth().onAuthStateChanged(function(user) {

          if (user) {
 
             
            // User is signed in.
            var displayName = user.displayName;
            var email = user.email;
            var emailVerified = user.emailVerified;
            var photoURL = user.photoURL;
            var uid = user.uid;
            var phoneNumber = user.phoneNumber;
            var providerData = user.providerData;
            user.getIdToken().then(function(accessToken) {
              document.getElementById('sign-in-status').textContent = 'Signed in';
              document.getElementById('sign-in').textContent = 'Sign out';
              document.getElementById('account-details').textContent = JSON.stringify({
                displayName: displayName,
                email: email,
                emailVerified: emailVerified,
                phoneNumber: phoneNumber,
                photoURL: photoURL,
                uid: uid,
                accessToken: accessToken,
                providerData: providerData
              }, null, '  ');
            });
        fetch.fetchFirebase();
          } else {
            // User is signed out.
            document.getElementById('sign-in-status').textContent = 'Signed out';
            document.getElementById('sign-in').textContent = 'Sign in';
            document.getElementById('account-details').textContent = 'null';
            
          }
        }, function(error) {
          console.log(error);
        });
      }

      window.addEventListener('load', function() {
      initApp();


        
      });
   
