import 'dart:developer' as developer;
import 'package:http/http.dart' as http;
import 'dart:convert';

// Simple function, if connects to backend, then puts new likes at route of that post's id

void updateLikes(int newLikes, int id) async { // this should probably return a response but for now its void
  developer.log("Update likes called");
  final response = await http.put(
    Uri.parse('https://team-bug.dokku.cse.lehigh.edu/posts/$id'),
    body: jsonEncode(<String, int>{
      'mLikes': newLikes
    }),
  );
  if (response.statusCode != 200) {
    throw Exception('Did not receive success status code from put request.');
  }
}