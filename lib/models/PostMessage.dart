import 'dart:developer' as developer;
import 'package:http/http.dart' as http;
import 'dart:convert';

// Simple function. If connects, then posts a JSON object with new message and sets likes = 0
// Could return the response but seems unimportant now.

void createNewMessage(String newMessage) async {
  // this should probably return a response but for now its void
  developer.log("Create new message called");
  final response = await http.post(
    Uri.parse('https://team-bug.dokku.cse.lehigh.edu/posts'),
    body: jsonEncode(<String, dynamic>{
      'mMessage': newMessage,
      'mLikes': 0,
    }),
  );
  if (response.statusCode != 200) {
    throw Exception('Did not receive success status code from post request.');
  }
}
