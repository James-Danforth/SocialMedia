var describe: any;
var it: any;
var expect: any;
describe("Tests of basic math functions", function() {
    it("Adding 1 should work", function() {
        var foo = 0;
        foo += 1;
        expect(foo).toEqual(1);
    });

    it("Subtracting 1 should work", function () {
        var foo = 0;
        foo -= 1;
        expect(foo).toEqual(-1);
    });

    it("UI Test: Add Button Hides Listing", function(){
        // click the button for showing the add button
        (<HTMLElement>document.getElementById("showFormButton")).click();
        // expect that the add form is not hidden
        expect( (<HTMLElement>document.getElementById("addElement")).style.display ).toEqual("block");
        // expect that the element listing is hidden
        expect( (<HTMLElement>document.getElementById("showElements")).style.display ).toEqual("none");
        // reset the UI, so we don't mess up the next test
  (<HTMLElement>document.getElementById("addCancel")).click();  
    });

    
    it("Should toggle 'isLiked' class when clicked", function() {
        // create a like button
        var likeButton = document.createElement("button");
        likeButton.classList.add("likebtn");
  
        // initial state should not contain isLiked
        expect(likeButton.classList.contains("isLiked")).toBe(false);
  
        // click the button
        likeButton.click();
  
        // add isLiked when clicked
        expect(likeButton.classList.contains("isLiked")).toBe(true);
  
        // click again should remove isLiked
        likeButton.click();
        expect(likeButton.classList.contains("isLiked")).toBe(false);
      });
});