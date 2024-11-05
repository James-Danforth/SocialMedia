import 'dart:developer' as developer;
import 'package:http/http.dart' as http;
import 'dart:convert';

// GetMessage class, allows us to store contents of each message (message, likes & id)
// fromJson class maps JSON object to fields, stores each GetMessage object in a list
// Gave every object isLiked field, which is reversed everytime like is pressed, so
// can remove likes. Had to remove const field from GetMessage constructor, since
// isLiked is updated each time like is pressed. This will not work with multiple users,
// so might consider adding isLiked field to database and doing put operation instead.
// Works since client and server side are currently on the same machine, but won't work
// with multiple users.

class GetMessage {
  final String message; 
  final int likes; 
  final int id;
  bool isLiked;

  GetMessage({ // GetMessage constructor, holding id, message and likes
    required this.message,
    required this.likes, 
    required this.id,
    this.isLiked = false
  });

  factory GetMessage.fromJson(Map<String, dynamic> json) { // converting decoded JSON to object
    return GetMessage(
      message: json['mContent'],
      likes: json['mLikes'], 
      id: json['mId'],
    ); 
  }
}

// Asynchronous call to get posts, if connects (statuscode == 200), then checks if list or map
// and handles each. Returns a list of GetMessage objects when done. Since each message is
// stored at the posts id, uses a loop to update url to route of each post, and then
// gather information from there.

Future<List<GetMessage>> fetchGetMessage() async {
  final response = await http // if successful, response holds JSON object as well as status code
      .get(Uri.parse('https://team-bug.dokku.cse.lehigh.edu/posts'));  // inserting database for get

  if (response.statusCode == 200) {
    // If the server did return a 200 OK response, then parse the JSON.
    final List<GetMessage> returnData = []; // initializing list of GetMessage objects returnData

    developer.log(response.body); // logging JSON object

    var res = jsonDecode(response.body); // decoding JSON

    if (res is Map && res['mData'] is List) { // if object is map (JSON? usually is), and has list of entries
      final List<dynamic> posts = res['mData']; // dynamic list of all posts so can loop through and grab id
      for (var postData in posts) { // looping through all posts
        final int postId = postData['mId']; // saving post id to add to url
        final postResponse = await http.get(Uri.parse('https://team-bug.dokku.cse.lehigh.edu/posts/$postId'));
        
        if (postResponse.statusCode == 200) {
          var postDetails = jsonDecode(postResponse.body)['mData']; // decoding body of post
          if (postDetails is Map<String, dynamic>) {
            returnData.add(GetMessage.fromJson(postDetails)); // create GetMessage with current post (likes, id, message), add to returnData
          }
        } else {
          developer.log('Failed to fetch details for post ID: $postId');
        }
      }
    } else {
      developer.log('ERROR: Unexpected json response type');
    }
    return returnData;

  } else {
    // If the server did not return a 200 OK response,
    // then throw an exception.
    throw Exception('Did not receive success status code from get request.');
  }
}