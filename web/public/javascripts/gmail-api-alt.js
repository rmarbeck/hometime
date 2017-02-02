	  var API_KEY = 'AIzaSyBILpKrEOIAOcjVBa9-ownlM_oiEQqBTyc';
	  
	  var CLIENT_ID = '731010313687-e6alf7us9flco0o571d88ghhc0rmjarr.apps.googleusercontent.com';

      var DISCOVERY_DOCS = ["https://www.googleapis.com/discovery/v1/apis/gmail/v1/rest"];

      var SCOPES = 'https://www.googleapis.com/auth/gmail.send';

      var authorizeButton = document.getElementById('authorize-button');
      var signoutButton = document.getElementById('signout-button');
      
      var signedInEmail;

      /**
       *  On load, called to load the auth2 library and API client library.
       */
      function handleClientLoad() {
        gapi.load('client:auth2', initClient);
      }

      /**
       *  Initializes the API client library and sets up sign-in state
       *  listeners.
       */
      function initClient() {
        gapi.client.init({
          apiKey: API_KEY,
          discoveryDocs: DISCOVERY_DOCS,
          clientId: CLIENT_ID,
          scope: SCOPES
        }).then(function () {
          // Listen for sign-in state changes.
          gapi.auth2.getAuthInstance().isSignedIn.listen(updateSigninStatus);

          // Handle the initial sign-in state.
          updateSigninStatus(gapi.auth2.getAuthInstance().isSignedIn.get());
          authorizeButton.onclick = handleAuthClick;
          signoutButton.onclick = handleSignoutClick;
        });
      }

      /**
       *  Called when the signed in status changes, to update the UI
       *  appropriately. After a sign-in, the API is called.
       */
      function updateSigninStatus(isSignedIn) {
        if (isSignedIn && checkUserSignedIn() == 0) {
          authorizeButton.style.display = 'none';
          signoutButton.style.display = 'block';
          defaultActionAfterSignin();
        } else {
          console.log("notSignedIn");
          authorizeButton.style.display = 'block';
          signoutButton.style.display = 'none';
        }
      }
      
      function checkUserSignedIn() {
	  	signedInEmail = gapi.auth2.getAuthInstance().currentUser.get().getBasicProfile().getEmail();
	  	if (signedInEmail === 'hometimefr@gmail.com') {
	  		return 0;
	  	} else {
	  		gapi.auth2.getAuthInstance().disconnect();
	  		console.log("Erreur : "+signedInEmail+" ne correspond pas");
	  		return -1;
	  	}
	  }

      /**
       *  Sign in the user upon button click.
       */
      function handleAuthClick(event) {
        gapi.auth2.getAuthInstance().signIn();
      }

      /**
       *  Disconnect the user upon button click.
       */
      function handleSignoutClick(event) {
        gapi.auth2.getAuthInstance().disconnect();
      }