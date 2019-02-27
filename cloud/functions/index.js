const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.newUser = functions.database.ref('/users/{macID}').onCreate(event => {
		const totalNumberRef = admin.database().ref('/totalusers');
		return totalNumberRef.once("value")
			.then(function(snapshot){
				var exists = snapshot.exists();
				if(exists){
					return totalNumberRef.transaction(function(TotalNumber){
						return (TotalNumber || 0) + 1;
					});
				} else {
					const usersRef = admin.database().ref('users');
					return usersRef.once('value').then(data => totalNumberRef.set(data.numChildren()));
				}
			});
    });
	
exports.deleteUser = functions.database.ref('/users/{macID}').onDelete(event => {
		const totalNumberRef = admin.database().ref('/totalusers');
		return totalNumberRef.once("value")
			.then(function(snapshot){
				var exists = snapshot.exists();
				if(exists){
					return totalNumberRef.transaction(function(TotalNumber){
						return (TotalNumber || 0) - 1;
					});
				} else {
					const usersRef = admin.database().ref('users');
					return usersRef.once('value').then(data => totalNumberRef.set(data.numChildren()));
				}
			});
    });