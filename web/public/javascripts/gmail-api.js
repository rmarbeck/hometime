	  var CLIENT_ID = '731010313687-e6alf7us9flco0o571d88ghhc0rmjarr.apps.googleusercontent.com';

      var SCOPES = ['https://www.googleapis.com/auth/gmail.compose'];

      /**
       * Check if current user has authorized this application.
       */
      function checkAuth() {
        gapi.auth.authorize(
          {
            'client_id': CLIENT_ID,
            'scope': SCOPES.join(' '),
            'immediate': true
          }, handleAuthResult);
      }

      /**
       * Handle response from authorization server.
       *
       * @@param {Object} authResult Authorization result.
       */
      function handleAuthResult(authResult) {
        var authorizeDiv = document.getElementById('authorize-div');
        if (authResult && !authResult.error) {
          // Hide auth UI, then load client library.
          authorizeDiv.style.display = 'none';
          loadGmailApi();
        } else {
          // Show auth UI, allowing the user to initiate authorization by
          // clicking authorize button.
          authorizeDiv.style.display = 'inline';
        }
      }

      /**
       * Initiate auth flow in response to user clicking authorize button.
       *
       * @@param {Event} event Button click event.
       */
      function handleAuthClick(event) {
        gapi.auth.authorize(
          {client_id: CLIENT_ID, scope: SCOPES, immediate: false},
          handleAuthResult);
        return false;
      }

      /**
       * Load Gmail API client library. List labels once client library
       * is loaded.
       */
      function loadGmailApi() {
        //gapi.client.load('gmail', 'v1', console.log('Gmail API loaded '));
        gapi.client.load('gmail','v1', function(){
 				var request = gapi.client.gmail.users.getProfile({
          			'userId': 'me'
           		});
 				request.execute(function(resp) {
   					console.log('Retrieved profile for:' + resp.emailAddress);
   					console.log('Retrieved profile for:' + resp);
 				});
		});
      }
      
      /**
       * Create draft
       */
      function createDraft() {
        var request = gapi.client.gmail.users.drafts.create({
          'userId': 'me',
          'message': {
                'raw': window.btoa("From: me\r\nTo:" + "hello@@person.com" + "\r\nSubject:"+ "subject" + "\r\n\r\n" + "message")
           }
        });

        request.execute(function(resp) {
            var pre = document.getElementById('output');
	        var textContent = document.createTextNode('OK \n');
        	pre.appendChild(textContent);
        });
      }