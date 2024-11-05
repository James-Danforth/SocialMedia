// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
var $: any;

// The 'this' keyword does not behave in JavaScript/TypeScript like it does in
// Java.  Since there is only one NewEntryForm, we will save it to a global, so
// that we can reference it from methods of the NewEntryForm in situations where
// 'this' won't work correctly.
var newEntryForm: NewEntryForm;

// This constant indicates the path to our back-end server (change to your own)
//const backendUrl = "https://2024sp-tutorial-mis326.dokku.cse.lehigh.edu";
const backendUrl = "https://team-bug.dokku.cse.lehigh.edu";

/**
 * NewEntryForm encapsulates all of the code for the form for adding an entry
 */
class NewEntryForm {
    /**
     * To initialize the object, we say what method of NewEntryForm should be
     * run in response to each of the form's buttons being clicked.
     */
    constructor() {
        document.getElementById("addCancel")?.addEventListener("click", (e) => {newEntryForm.clearForm();});
        document.getElementById("addButton")?.addEventListener("click", (e) => {newEntryForm.submitForm();});
    }

    /**
     * Clear the form's input fields
     */
    clearForm() {
        //(<HTMLInputElement>document.getElementById("newTitle")).value = "";
        (<HTMLInputElement>document.getElementById("newMessage")).value = "";
        (<HTMLElement>document.getElementById("showElements")).style.display = "block";
        (<HTMLElement>document.getElementById("addElement")).style.display = "none";
    }

    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    submitForm() {
        console.log("Submit form called.");
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        //let title = "" + (<HTMLInputElement>document.getElementById("newTitle")).value;
        let msg = "" + (<HTMLInputElement>document.getElementById("newMessage")).value;
        if ( msg === "") {``
            window.alert("Error: message is not valid");
            return;
        }
    
        // set up an AJAX POST. 
        // When the server replies, the result will go to onSubmitResponse
        const doAjax = async () => {
            await fetch(`${backendUrl}/posts`, {
                method: 'POST',
                body: JSON.stringify({
                    mMessage: msg,
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok for posts POST: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                newEntryForm.onSubmitResponse(data);
                console.log(data);
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }
    
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * 
     * @param data The object returned by the server
     */
    private onSubmitResponse(data: any) {
        // If we get an "ok" message, clear the form
        if (data.mStatus === "ok") {
            newEntryForm.clearForm();
            mainList.refresh();
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }
} // end class NewEntryForm

/**
 * ShowPost shows a post and all its comments
 */

var showPost: ShowPost;

class ShowPost {

    private postId: number;
    private postMessage: string;

    /**
     * Refreshes show post to get all comments on a page
     */
    refresh(id: number, message: string) {
        const postContentElement = document.getElementById("postContent");
        this.postId = id;
        this.postMessage = message;

        if (postContentElement) { // adds messgae text to top
            postContentElement.innerHTML = "";
            const messageDiv = document.createElement("div");
            messageDiv.textContent = message;
            postContentElement.appendChild(messageDiv);
        }

        const doAjax = async () => {
            await fetch(`${backendUrl}/comments/${id}`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, clear the form
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok for comments GET: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                showPost.update(data, id);
                console.log(data);
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * Creates table of comments to add to showPost
     */
    private async update(data: any, id: number) {
        let elem_commentList = document.getElementById("commentList");
    
        if (elem_commentList != null) {
            elem_commentList.innerHTML = "";
    
            let table = document.createElement('table');
    
            async function fetchCommentDetails(i) {
                if (i >= data.mData.length) {
                    if(elem_commentList) {
                        elem_commentList.appendChild(table);
                        return;
                    }
                }
    
                let commentId = data.mData[i].mId;
                let commentContent = data.mData[i].mContent;
    
                let tr = document.createElement('tr');
    
                // Create td for user
                let td_user = document.createElement('td');
                td_user.textContent = data.mData[i].mUser;
                td_user.classList.add("comment-user");
                td_user.setAttribute('comment-user', data.mData[i].mUser);
                td_user.setAttribute('data-value', commentId);
                tr.appendChild(td_user);

                // Create td for comment content
                let td_comment = document.createElement('td');
                td_comment.textContent = commentContent;
                td_comment.classList.add("postContent");
                td_comment.setAttribute('data-value', commentId);
                tr.appendChild(td_comment);
    
                // Create td for edit button
                let td_edit = document.createElement('td');
                let btn = document.createElement('button'); // add if condition, if comment poster id does not match global current user id, don't add edit button
                btn.classList.add("editbtn"); // only assign edit button if poster id = current logged in user id
                btn.setAttribute('data-value', commentId);
                btn.innerHTML = '✎';
                td_edit.appendChild(btn);
                tr.appendChild(td_edit);
    
                table.appendChild(tr);
    
                await fetchCommentDetails(i + 1);
            }
            await fetchCommentDetails(0);
        }
        
        // Find all of the edit buttons, and set their behavior
        const all_editbtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("editbtn"));
        for (let i = 0; i < all_editbtns.length; ++i) {
            all_editbtns[i].addEventListener("click", (e) => {showPost.clickEdit( e );});
        }

        // Find all of the users, and set their behavior
        const all_users = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("comment-user"));
        for (let i = 0; i < all_users.length; ++i) {
            all_users[i].addEventListener("click", (e) => {
                const commentUser = all_users[i].getAttribute('comment-user');
                if(commentUser) {
                    showProfile.refresh(parseInt(commentUser));
                }
            });
        }
    }    

    /**
     * clickEdit is the code we run in response to a click of an edit button on a comment
     */
     private clickEdit(e: Event) {
         // we need the ID of the row
         const id = (<HTMLElement>e.target).getAttribute("data-value");

         // Issue an AJAX GET and then pass the result to editEntryForm.init()
         const doAjax = async () => {
             await fetch(`${backendUrl}/comments/${id}`, {
                 method: 'GET',
                 headers: {
                     'Content-type': 'application/json; charset=UTF-8'
                 }
             }).then( (response) => {
                 if (response.ok) {
                     return Promise.resolve( response.json() );
                 }
                 else{
                     window.alert(`The server replied not ok for comments GET: ${response.status}\n` + response.statusText);
                 }
                 return Promise.reject(response);
             }).then( (data) => {
                 editEntryForm.init(data);
                 console.log(data);
             }).catch( (error) => {
                 console.warn('Something went wrong.', error);
                 window.alert("Unspecified error");
             });
         }

         // make the AJAX post and output value or error message to console
         doAjax().then(console.log).catch(console.log);
     }

    constructor() {
        document.getElementById("commentCancel")?.addEventListener("click", (e) => {showPost.clearForm();});
        document.getElementById("commentButton")?.addEventListener("click", (e) => {showPost.submitForm();});
    }

    /**
     * Clear the form's input fields
     */
    clearForm() {
        //(<HTMLInputElement>document.getElementById("newTitle")).value = "";
        (<HTMLInputElement>document.getElementById("newComment")).value = "";
        hideAll();
        (<HTMLElement>document.getElementById("showElements")).style.display = "block";
    }

    /**
     * Submit form and do AJAX post for comment
     */
    submitForm() {
        console.log("Submit form called.");
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        let msg = "" + (<HTMLInputElement>document.getElementById("newComment")).value;
        if ( msg === "") {``
            window.alert("Error: message is not valid");
            return;
        }
    
        // set up an AJAX POST. 
        // When the server replies, the result will go to onSubmitResponse
        const doAjax = async () => {
            await fetch(`${backendUrl}/comments`, {
                method: 'POST',
                body: JSON.stringify({
                    mMessage: msg,
                    mPostId: this.postId
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok for comments POST: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                showPost.onSubmitResponse(data);
                console.log(data);
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }
    
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * 
     * @param data The object returned by the server
     */
    private onSubmitResponse(data: any) {
        // If we get an "ok" message, clear the form
        if (data.mStatus === "ok") {
            showPost.clearForm();
            showPost.refresh(this.postId, this.postMessage);
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }
} // end class ShowPost

// a global for the main ElementList of the program.  See newEntryForm for 
// explanation
var mainList: ElementList;

/**
 * ElementList provides a way of seeing all of the data stored on the server.
 */
class ElementList {
    /**
     * refresh is the public method for updating messageList
     */
    refresh() {
        // Issue an AJAX GET and then pass the result to update(). 
        const doAjax = async () => {
            await fetch(`${backendUrl}/posts`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, clear the form
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok for posts GET: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                mainList.update(data);
                console.log(data);
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * Update is whats called when refresh returns data, creates table of posts
     */
    private async update(data: any) {
        let elem_messageList = document.getElementById("messageList");

        if(elem_messageList != null) {
            elem_messageList.innerHTML = "";

            let fragment = document.createDocumentFragment();
            let table = document.createElement('table');

            async function fetchMessageDetails(i) { 
                if (i >= data.mData.length) {
                    fragment.appendChild(table);
                    elem_messageList!.appendChild(fragment);
                    return;
                }
    
                let messageId = data.mData[i].mId;

                try {
                    let response = await fetch(`${backendUrl}/posts/${messageId}`, {
                        method: 'GET',
                        headers: {
                            'Content-type': 'application/json; charset=UTF-8'
                        }
                    });
                    if (response.ok) {
                        let messageDetails = await response.json();
                        if (messageDetails.mStatus === 'ok' && messageDetails.mData) {
                            let messageContent = messageDetails.mData.mContent;
                            let messageUser = messageDetails.mData.mUser;
                            //console.log("messgae content: "+messageContent);

                            let tr = document.createElement('tr'); // adding message field

                            let td_user = document.createElement('td');
                            td_user.textContent = messageUser;
                            td_user.classList.add("postUser");
                            td_user.setAttribute('data-value', messageId);
                            td_user.setAttribute('data-user', messageUser);
                            tr.appendChild(td_user);

                            let td_message = document.createElement('td');
                            td_message.textContent = messageContent;
                            td_message.classList.add("postContent");
                            td_message.setAttribute('data-value', messageId);
                            td_message.setAttribute('data-content', messageContent);
                            tr.appendChild(td_message);

                            let td_likes = document.createElement('td'); // adding likes field
                            //console.log("likes = " + data.mData[i].mLikes + " dislikes = " + data.mData[i].mDislikes);
                            td_likes.textContent = data.mData[i].mLikes + (data.mData[i].mDislikes * -1);
                            tr.appendChild(td_likes);

                            let fragment = mainList.buttons(messageId, messageContent); // call buttons function
                            tr.appendChild(fragment);
                            table.appendChild(tr);
                        } else {
                            throw new Error(`Invalid response format for message ID: ${messageId}`);
                        }
                    } else {
                        throw new Error(`Failed to fetch message details for ID: ${messageId}`);
                    }
                } catch (error) {
                    console.error(`Error fetching message details for ID: ${messageId}`, error);
                }
                await fetchMessageDetails(i+1);
            }
            await fetchMessageDetails(0);
        }

        // Find all of the post contents, and set their behavior
        const all_postContents = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("postContent"));
        for (let i = 0; i < all_postContents.length; ++i) {
            all_postContents[i].addEventListener("click", (e) => {
                const messageId = all_postContents[i].getAttribute('data-value');
                const messageContent = all_postContents[i].getAttribute('data-content');
                if(messageId && messageContent) {
                    (<HTMLElement>document.getElementById("showPost")).style.display = "block";
                    (<HTMLElement>document.getElementById("showElements")).style.display = "none";
                    showPost.refresh(parseInt(messageId), messageContent);
                }
            });
        } 

        // Find all of the comment buttons, and set their behavior
        const all_cmntbtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("cmntbtn"));
        for (let i = 0; i < all_cmntbtns.length; ++i) {
            all_cmntbtns[i].addEventListener("click", (e) => {
                const messageId = all_cmntbtns[i].getAttribute('data-value');
                const messageContent = all_cmntbtns[i].getAttribute('data-content');
                if(messageId && messageContent) {
                    (<HTMLElement>document.getElementById("showPost")).style.display = "block";
                    (<HTMLElement>document.getElementById("showElements")).style.display = "none";
                    showPost.refresh(parseInt(messageId), messageContent);
                }
            });
        } 

        // Find all of the like buttons, and set their behavior
        const all_likebtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("likebtn"));
        for (let i = 0; i < all_likebtns.length; ++i) {
            all_likebtns[i].addEventListener("click", (e) => {mainList.clickLike(e, data.mData[i].mLikes, data.mData[i].mDislikes);});
            all_likebtns[i].classList.add("up-empty");
        }

        // Find all the dislike buttons, and set their behavior
        const all_dislikebtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("dislikebtn"));
        for (let i = 0; i < all_dislikebtns.length; ++i) { // needs work
            all_dislikebtns[i].addEventListener("click", (e) => {mainList.clickDisLike(e, data.mData[i].mLikes, data.mData[i].mDislikes);});
            all_dislikebtns[i].classList.add("down-empty");
        }

        // Find all of the users, and set their behavior
        const all_users = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("data-user"));
        for (let i = 0; i < all_users.length; ++i) {
            all_users[i].addEventListener("click", (e) => {
                const commentUser = all_users[i].getAttribute('data-user');
                if(commentUser) {
                    showProfile.refresh(parseInt(commentUser));
                }
            });
        }
    }

    /**
     * clickLike is the code we run in response to a click of a like button
     */
    private clickLike(e: Event, likes: number, dislikes: number) {
        const up = <HTMLElement>e.target;
        const id = (<HTMLElement>e.target).getAttribute("data-value");
        const down = document.querySelector(`.dislikebtn[data-value="${id}"]`) as HTMLElement;
        let body = {};

        if(up.classList.contains("up-empty") && down.classList.contains("down-empty")) {
            likes += 1; 
            body = { mLikes: likes }; // checking if both are empty
            
            up.classList.remove("up-empty");
            up.classList.add("up-filled");
        } 
        else if (down.classList.contains("down-filled") && up.classList.contains("up-empty")) { 
            likes += 1; dislikes -= 1;
            body = { mLikes: likes, mDislikes: dislikes }; // checking for if down is pressed

            down.classList.remove("down-filled");
            down.classList.add("down-empty");
            up.classList.remove("up-empty");
            up.classList.add("up-filled");
        }
        else if (up.classList.contains("up-filled") && down.classList.contains("down-empty")) {
            likes -= 1;
            body = { mLikes: likes };
            
            up.classList.remove("up-filled");
            up.classList.add("up-empty");
        }
        else {
            console.log("Error: Both like and dislike pressed");
        }

        const doAjax = async () => {
            await fetch(`${backendUrl}/posts/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                },
                body: JSON.stringify(body)
            }).then((response) => {
                if (response.ok) {
                    return Promise.resolve(response.json());
                } else {
                    window.alert(`The server replied not ok for likes PUT: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                console.log(data); // Optional: Log the response data
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);

        // TODO: Consider refactoring this repetitive pattern into a reusable function
    }

    /**
     * clickDisLike is the code we run in response to a click of a dislike button
     */
    private clickDisLike(e: Event, likes: number, dislikes: number) {
        const down = <HTMLElement>e.target;
        const id = (<HTMLElement>e.target).getAttribute("data-value");
        const up = document.querySelector(`.likebtn[data-value="${id}"]`) as HTMLElement; 
        let body = {};

        if(up.classList.contains("up-empty") && down.classList.contains("down-empty")) {
            dislikes += 1; 

            body = { mDislikes: dislikes }; // checking if both are empty
            
            down.classList.remove("down-empty");
            down.classList.add("down-filled");
        } 
        else if (up.classList.contains("up-filled") && down.classList.contains("down-empty")) { 
            likes -= 1; dislikes += 1;
            body = { mLikes: likes, mDislikes: dislikes }; // checking for if down is pressed

            up.classList.remove("up-filled");
            up.classList.add("up-empty");
            down.classList.remove("down-empty");
            down.classList.add("down-filled");
        }
        else if (down.classList.contains("down-filled") && up.classList.contains("up-empty")) {
            dislikes -= 1;
            body = { mDislikes: dislikes };
            
            down.classList.remove("down-filled");
            down.classList.add("down-empty");
        }
        else {
            console.log("Error: Both like and dislike pressed");
        }

        const doAjax = async () => {
            await fetch(`${backendUrl}/posts/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                },
                body: JSON.stringify(body)
            }).then((response) => {
                if (response.ok) {
                    return Promise.resolve(response.json());
                } else {
                    window.alert(`The server replied not ok for dislikes PUT: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                console.log(data); // Optional: Log the response data
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
        // TODO: Consider refactoring this repetitive pattern into a reusable function
    }
    
    /**
     * buttons() adds a like button, dislike button and an comment button to the HTML for each row
     */
    private buttons(id: string, message: string): DocumentFragment { // not used rn
        let fragment = document.createDocumentFragment();
        let td = document.createElement('td');

        // create like button, add to new td, add td to returned fragment
        td = document.createElement('td');
        let btn = document.createElement('button');
        btn = document.createElement('button');
        btn.classList.add("likebtn");
        btn.setAttribute('data-value', id);
        btn.innerHTML = '&#x21E7;'; // Up arrow
        td.appendChild(btn);
        fragment.appendChild(td);

        // create dislike button, add to new td, add td to returned fragment
        td = document.createElement('td');
        btn = document.createElement('button');
        btn.classList.add("dislikebtn");
        btn.setAttribute('data-value', id);
        btn.innerHTML = '&#x21E9;'; // Down arror
        td.appendChild(btn);
        fragment.appendChild(td);

        // create comment button, add to new td, add td to returned fragment
        td = document.createElement('td');
        btn = document.createElement('button');
        btn.classList.add("cmntbtn");
        btn.setAttribute('data-value', id);
        btn.setAttribute('data-content', message);
        btn.innerHTML = 'Comment';
        td.appendChild(btn);
        fragment.appendChild(td);

        return fragment;
    }

    
} // end class ElementList

// a global for the EditEntryForm of the program.  See newEntryForm for explanation
var editEntryForm: EditEntryForm;

/**
 * EditEntryForm encapsulates all of the code for the form for editing an entry
 */
class EditEntryForm { 
    /**
     * To initialize the object, we say what method of EditEntryForm should be
     * run in response to each of the form's buttons being clicked.
     */
    constructor() {
        document.getElementById("editCancel")?.addEventListener("click", (e) => {editEntryForm.clearForm();});
        document.getElementById("editButton")?.addEventListener("click", (e) => {editEntryForm.submitForm();});
    }

    /**
     * init() is called from an AJAX GET, and should populate the form if and 
     * only if the GET did not have an error
     */
    init(data: any) {
        // If we get an "ok" message, fill in the edit form
        if (data.mStatus === "ok") {
            (<HTMLInputElement>document.getElementById("editMessage")).value = data.mData.mContent;
            (<HTMLInputElement>document.getElementById("editId")).value = data.mData.mId;
            (<HTMLInputElement>document.getElementById("editCreated")).value = data.mData.mCreated;
            // show the edit form
            hideAll();
            (<HTMLElement>document.getElementById("editElement")).style.display = "block";
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }

    /**
     * Clear the form's input fields
     */
    clearForm() {
        (<HTMLInputElement>document.getElementById("editMessage")).value = "";
        (<HTMLInputElement>document.getElementById("editId")).value = "";
        (<HTMLInputElement>document.getElementById("editCreated")).value = "";
        hideAll();
        (<HTMLElement>document.getElementById("showPost")).style.display = "block";
    }

    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    submitForm() {
        console.log("Submit edit form called.");
        // get the values of the two fields, force them to be strings, and check
        // that neither is empty
        let msg = "" + (<HTMLInputElement>document.getElementById("editMessage")).value;
        // NB: we assume that the user didn't modify the value of editId
        let id = "" + (<HTMLInputElement>document.getElementById("editId")).value;
        
        if (msg === "" || id === "") { 
            window.alert("Error: message is not valid");
            return;
        }

        // set up an AJAX PUT.
        // When the server replies, the result will go to onSubmitResponse
        const doAjax = async () => {
            await fetch(`${backendUrl}/comments/${id}`, {
                method: 'PUT',
                body: JSON.stringify({
                    mMessage: msg
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    // return response.json();
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok for comments PUT: ${response.status}\n` + response.statusText);
                }
                // return response;
                return Promise.reject(response);
            }).then( (data) => {
                editEntryForm.onSubmitResponse(data);
                console.log(data);
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * 
     * @param data The object returned by the server
     */
    private onSubmitResponse(data: any) {
        // If we get an "ok" message, clear the form and refresh the main 
        // listing of messages
        if (data.mStatus === "ok") {
            editEntryForm.clearForm();
            mainList.refresh();
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }
} // end class EditEntryForm

var showProfile: ShowProfile;

class ShowProfile {
    private loggedIn: boolean;

    /**
     * Refresh initiates an AJAX get to fill page
     */
    refresh(passedUserId: number) {

        if(passedUserId == userId) {
            console.log("logged in");
            this.loggedIn = true;
        } else {
            this.loggedIn = false;
        }

        if(this.loggedIn) {
            let profileEditDiv = document.getElementById("profileEditDiv");
            if(profileEditDiv) {
                let profileEditButton = document.createElement("button");
                profileEditButton.id = "profileEdit";
                profileEditButton.textContent = "✎";
                profileEditDiv.appendChild(profileEditButton);
            }
        } else {
            let profileEditDiv = document.getElementById("profileEditDiv");
            if(profileEditDiv) {
                profileEditDiv.innerHTML = "";
            }
        }

        // Issue an AJAX GET and then pass the result to update(). 
        const doAjax = async () => {
            await fetch(`${backendUrl}/users/${userId}`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, clear the form
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok for users GET: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                showProfile.update(data, userId);
                console.log(data);
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * Update is what's called when AJAX returns profile, creates table
     */
    private async update(data: any, userId: number) {
        let elem_profileDetails = document.getElementById("profileDetails");

        if(elem_profileDetails != null) {
            elem_profileDetails.innerHTML = "";

            let table = document.createElement('table');

            async function fetchProfileDetails() { 
                try {
                    let response = await fetch(`${backendUrl}/users/${userId}`, {
                        method: 'GET',
                        headers: {
                            'Content-type': 'application/json; charset=UTF-8'
                        }
                    });
                    if (response.ok) {
                        let profileDetails = await response.json();
                        if (profileDetails.mStatus === 'ok' && profileDetails.mData) {
                            let tr_name = document.createElement('tr');
                            let td_nameLabel = document.createElement('td');
                            td_nameLabel.textContent = "Username:"
                            tr_name.appendChild(td_nameLabel);
                            let td_nameValue = document.createElement('td');
                            td_nameValue.textContent = profileDetails.mData.username;
                            tr_name.appendChild(td_nameValue);
                            table.appendChild(tr_name);

                            let tr_email = document.createElement('tr');
                            let td_emailLabel = document.createElement('td');
                            td_emailLabel.textContent = "Email:"
                            tr_email.appendChild(td_emailLabel);
                            let td_emailValue = document.createElement('td');
                            td_emailValue.textContent = profileDetails.mData.email;
                            tr_email.appendChild(td_emailValue);
                            table.appendChild(tr_email);

                            if(this.loggedIn) {
                                let tr_GI = document.createElement('tr');
                                let td_GILabel = document.createElement('td');
                                td_GILabel.textContent = "Gender:"
                                tr_GI.appendChild(td_GILabel);
                                let td_GIValue = document.createElement('td');
                                td_GIValue.textContent = "male";
                                tr_GI.appendChild(td_GIValue);
                                table.appendChild(tr_GI);
                
                                let tr_SI = document.createElement('tr');
                                let td_SILabel = document.createElement('td');
                                td_SILabel.textContent = "Sexual Orientation:"
                                tr_SI.appendChild(td_SILabel);
                                let td_SIValue = document.createElement('td');
                                td_SIValue.textContent = "heterosexual";
                                tr_SI.appendChild(td_SIValue);
                                table.appendChild(tr_SI);
                            }

                            let tr_bio = document.createElement('tr');
                            let td_bioLabel = document.createElement('td');
                            td_bioLabel.textContent = "Note:"
                            tr_bio.appendChild(td_bioLabel);
                            let td_bioValue = document.createElement('td');
                            td_bioValue.textContent = profileDetails.mData.note;
                            tr_bio.appendChild(td_bioValue);
                            table.appendChild(tr_bio);

                            if(elem_profileDetails) {
                                elem_profileDetails.append(table);
                            }
                        } else {
                            throw new Error(`Invalid response format for profile ID: ${userId}`);
                        }
                    } else {
                        throw new Error(`Failed to fetch profile details for ID: ${userId}`);
                    }
                } catch (error) {
                    console.error(`Error fetching profile details for ID: ${userId}`, error);
                }
            }
            await fetchProfileDetails();
        }

        // Find edit button and set behavior
        const edit_button = (<HTMLInputElement>document.getElementById("profileEdit"));
        edit_button.addEventListener("click", (e) => {
            showProfile.clickEdit(userId); // currently not working lol
        });
    }

    /**
     * clickEdit is the code we run in response to a click of an edit profile button
     */
    clickEdit(userId: number) {
        // Issue an AJAX GET and then pass the result to editEntryForm.init()
        const doAjax = async () => {
            await fetch(`${backendUrl}/users/${userId}`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                else{
                    window.alert(`The server replied not ok for comments GET: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                editProfile.init(data);
                console.log(data);
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }
} // end class ShowProfile

var editProfile: EditProfile;

class EditProfile { 
    /**
     * To initialize the object, we say what method of EditEntryForm should be
     * run in response to each of the form's buttons being clicked.
     */
    constructor() {
        document.getElementById("noteCancel")?.addEventListener("click", (e) => {editProfile.clearForm();});
        document.getElementById("noteButton")?.addEventListener("click", (e) => {editProfile.submitForm();});
    }

    /**
     * init() is called from an AJAX GET, and should populate the form if and 
     * only if the GET did not have an error
     */
    init(data: any) {
        // If we get an "ok" message, fill in the edit form
        if (data.mStatus === "ok") {
            (<HTMLInputElement>document.getElementById("editNote")).value = data.mData.mNote;
            (<HTMLInputElement>document.getElementById("editUserId")).value = data.mData.muserId;
            (<HTMLInputElement>document.getElementById("editNoteCreated")).value = data.mData.mCreated;
            // show the edit form
            hideAll();
            (<HTMLElement>document.getElementById("editProfile")).style.display = "block";
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }

    /**
     * Clear the form's input fields
     */
    clearForm() {
        (<HTMLInputElement>document.getElementById("editNote")).value = "";
        (<HTMLInputElement>document.getElementById("editUserId")).value = "";
        (<HTMLInputElement>document.getElementById("editNoteCreated")).value = "";
        hideAll();
        (<HTMLElement>document.getElementById("profilePage")).style.display = "block";
    }

    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    submitForm() {
        console.log("Submit edit form called.");
        // get the values of the two fields, force them to be strings, and check
        // that neither is empty
        let note = "" + (<HTMLInputElement>document.getElementById("editNote")).value;
        // NB: we assume that the user didn't modify the value of editId
        let id = "" + (<HTMLInputElement>document.getElementById("editUserId")).value;
        
        if (note === "" || id === "") { 
            window.alert("Error: message is not valid");
            return;
        }

        // set up an AJAX PUT.
        // When the server replies, the result will go to onSubmitResponse
        const doAjax = async () => {
            await fetch(`${backendUrl}/users/${id}`, {
                method: 'PUT',
                body: JSON.stringify({
                    mNote: note
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    // return response.json();
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok for comments PUT: ${response.status}\n` + response.statusText);
                }
                // return response;
                return Promise.reject(response);
            }).then( (data) => {
                editProfile.onSubmitResponse(data, id);
                console.log(data);
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * 
     * @param data The object returned by the server
     */
    private onSubmitResponse(data: any, userId) {
        // If we get an "ok" message, clear the form and refresh the main 
        // listing of messages
        if (data.mStatus === "ok") {
            editProfile.clearForm();
            showProfile.refresh(userId);
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }
} // end class EditProfile



function hideAll() { // main function for setting up a page
    (<HTMLElement>document.getElementById("loginPage")).style.display = "none";
    (<HTMLElement>document.getElementById("editElement")).style.display = "none";
    (<HTMLElement>document.getElementById("addElement")).style.display = "none";
    (<HTMLElement>document.getElementById("showElements")).style.display = "none";
    (<HTMLElement>document.getElementById("profilePage")).style.display = "none";
    (<HTMLElement>document.getElementById("showPost")).style.display = "none";
    (<HTMLElement>document.getElementById("editProfile")).style.display = "none";
}

const userId = 1; // just for testing purposes, have backend return unique user id

// Run some configuration code when the web page loads
document.addEventListener('DOMContentLoaded', () => {
    // Create the object that controls the "New Entry" form
    newEntryForm = new NewEntryForm();
    // Create the object that controls the "Edit Entry" form
    editEntryForm = new EditEntryForm();
    // Create the object for the main data list, and populate it with data from the server
    mainList = new ElementList();
    mainList.refresh();
    // Create profile object, and id of user
    showProfile = new ShowProfile();
    
    showProfile.refresh(userId);
    // create show post object
    showPost = new ShowPost();
    // create edit profile object
    editProfile = new EditProfile();
    //window.alert('DOMContentLoaded');

    // set up initial UI state
    hideAll();
    (<HTMLElement>document.getElementById("showElements")).style.display = "block"; // change to login page
    // add code to have const userid = backend return

    // set up the "Add Message" button
    document.querySelectorAll(".showFormButton").forEach((button) => {
        button.addEventListener("click", (e) => {
            hideAll();
            (<HTMLElement>document.getElementById("addElement")).style.display = "block";
        });
    });

    // set up the "Show Profile" button
    document.querySelectorAll(".showProfileButton").forEach((button) => {
        button.addEventListener("click", (e) => {
            hideAll();
            (<HTMLElement>document.getElementById("profilePage")).style.display = "block";
            showProfile.refresh(userId);
        });
    });

    // set up "Home Button"
    document.querySelectorAll(".homeButton").forEach((button) => {
        button.addEventListener("click", (e) => {
            hideAll();
            (<HTMLElement>document.getElementById("showElements")).style.display = "block";
        });
    });
}, false);
