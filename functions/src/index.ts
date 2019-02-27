import * as functions from 'firebase-functions';
const admin = require("firebase-admin");
admin.initializeApp();

exports.updateCountOnCreateOrDelete = functions.database
  .ref("/users/{pushId}")
  .onWrite((change, context) => {
    // Only edit data when it is first created
    // if (!change.before.exists()) {
    //   return null;
    // }
    change.after.ref.parent
      .orderByChild("MAC")
      .once("value", snap => {
        let count = 0;
        snap.forEach(childsnap => {
          if (childsnap.val().MAC) { 
            count++;
          }
          return false;
        });
        change.after.ref.parent
          .child("totalusers")
          .set(count)
          .catch(err => console.log("error", err));
      })
      .catch(err => console.error("error1", err));
    
  });
// exports.updateCountOnDelete = functions.database
//     .ref("/users/{pushId}")
//     .onDelete((snapshot, context) => {
//     console.log("Uppercasing", context.params.pushId, snapshot.numChildren());
//       return snapshot.ref.parent.child("totalusers").set(snapshot.numChildren());
//   });
