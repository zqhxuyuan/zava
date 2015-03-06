#Programming Problem from TopCoder

##Array
1.  Given an array A[0, N-1] find the position of the element with the minimum value between two given indices.
    
    *HINT: use SegmentTree to achieve O(N) pre-process and O(lgN) query.*

##Geometry
        Basic Operation on Vector
            Vector(A, B) = float[]{B[0] - A[0], B[1] - A[1]}; 
            GeoUtil.distance(A, B) = Vector(A,B).length()
                                   = Math.sqrt(AB[0]^2 + AB[1]^2)
            GeoUtil.dot(A, B, C) = Vector(A, B).dot(Vector(B, C))           
                                 = (AB[0] * BC[0] + AB[1] * BC[1]) it's a value
                                 = Vector(A, B).length * Vector(B, C).length * Cos(θ), where θ is the angle between the AB and BC.
            GeoUtil.cross(A, B, C) = Vector(A, B).cross(Vector(B, C))
                                   = (AB[0] * BC[1]) - (AB[1] * BC[2]), it's a vector, the direction based on right-hand rule.
                                   = Vector(A, B).length * Vector(B, C).length * Sin(θ). |θ| is the angle between the two vectors.
                                 
1.  Given a line L(point A and B) and a point X, write code to find the distance of L and X.
    Also consider L as a segment, not a line.
    
    *HINT: if L is line, distance(L, X) is the area of polygon (AB, BX) / length(AB).
    if L is a segment, could check if perpendicular is out of range, if YES, distance = length(AX) or length(BX)
    
        if(isSegment){
            if(GeoUtil.dot(X, Y, Z) > 0) return GeoUtil.distance(Y, Z);
            if(GeoUtil.dot(Y, X, Z) > 0) return GeoUtil.distance(X, Z);
        }
        return Math.abs(GeoUtil.cross(X, Y, Z) / GeoUtil.distance(X, Y)); 
    *Code is in com.interview.basic.model.geometry.Line.distance(float[] point);*    
    
2.  Given a list points identify a polygon, write code to calculate the area of the polygon.

    *HINT: cross product is the area of parallelogram. 
    
        For any two vector ab.cross(bc) is the area of parallelogram created by (ab, bc), so the area of 
        triangle abc = ab.cross(bc)/2; 
            float area = 0;
            for(int i = 1; i < points.length - 1; i++){
                area += GeoUtil.cross(points[0], points[i], points[i+1]);
            }
            return Math.abs(area/2);
        And cross product is positive if C is in the right of ab, otherwise will be negative, so the solution
        also workable for non-convex polygon.
    *Code is in com.interview.basic.model.geometry.Polygon.distance(float[] point);*  
      
3.  Given two line, write code to find the intersection if have.
    
    *HINT: Line can be identified as Ax + By = C. The intersection can be found following both equation. 
    A1x + B1y = C1 and A2x + B2y = C2. so x' = (A1*C2 - A2*C1)/A1*B2 - A2*B1, and y' = (B2*C1 - B1*C2)/A1*B2 - A2*B1.
    If A1*B2 - A2*B1 == 0, means the two lines are parallel, no intersection.*
    
        Note: If give two point, need find the equation by solve A, B, C: A = y2-y1, B = x1-x2, C = A*x1+B*y1.
        If line is a segment, need check if intersection on the segments, by check x,y in range for both segment:
            min(x1, x2) <= intersection.x <= max(x1, x2) and min(y1, y2) <= intersection.y <= max(y1, y2)
    *Code is in com.interview.basic.model.geometry.Line.intersection(Line line);* 
    
4.  Given a line, write code to find the perpendicular line.

    *HINT: the perpendicular line is -Bx + Ay = D, D can be resolve by a given point or the midpoint of the line.*
    
    *Code is in com.interview.basic.model.geometry.Line.perpendicular() and perpendicular(float[] point); 

5.  Given three points A,B,C not in one line, find a Circle can go through all the three points.

    *HINT: Circle can be identify by center and radius. The center of circle can be found as the intersection of
    perpendicular through midpoint of AB and BC, and radius is distance of center to A or B or C.*
     
        Line ab = new Line(A, B);
        Line abPerpendicular = ab.perpendicular();
        Line bc = new Line(B, C);
        Line bcPerpendicular = bc.perpendicular();
        
        float[] center = abPerpendicular.intersection(bcPerpendicular);
        float radius = GeoUtil.distance(center, A);
    *Code is in com.interview.basic.model.geometry.Circle(float[] X, float[] Y, float[] Z);*   

6.  Given a point X and a Line L, find the reflection of X against L.
 
    *HINT: the reflection of X against L can be resolved by Y, Y is the intersection of L's perpendicular through X.
     reflection of X = Y - (X - Y).*
     
        Line perpendicular = L.perpendicular(X);
        float[] intersection = L.intersection(perpendicular);
        float[] reflection = new float[2];
        reflection[0] = intersection[0] * 2 - X[0];
        reflection[1] = intersection[1] * 2 - X[1];
    *Code is in com.interview.basic.model.geometry.Line.reflection(float[] point);*

7.  Given a point A, rotate the point by the center of B counterclockwise by θ degrees. 

    *HINT: A's rotation based on (0,0) counterclockwise by θ degrees is x' = x Cos(θ) - y Sin(θ), 
    and y' = x Sin(θ) + y Cos(θ). A's rotation based on B, can change the coordinate system using B as origin 
    by minus(A, B), and do rotation, then move back to (0,0) by plus(A, B).*
    
        minus(point, origin);
        double radians = Math.toRadians(degree);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        
        float[] rotated = new float[2];
        rotated[0] = (float) (point[0] * cos - point[1] * sin);
        rotated[1] = (float) (point[0] * sin - point[1] * cos);
        
        plus(rotated, origin);
        plus(point, origin);
    *Note: a vector is defined by a direction and a magnitude, not begin and end. so rotated of a vector
    counterclockwise by θ degrees, is rotated as one point is origin, just do x' = x Cos(θ) - y Sin(θ), 
    and y' = x Sin(θ) + y Cos(θ), only change the direction, magnitude still the same.*

8.  **Convex Hull** Given a list of points, find the convex hull. 
    A convex hull of a set of points is the smallest convex polygon that contains every one of the points.
    
    *HINT: start with leftmost point, then loop to find next point on convex hull while it not leftmost.
    In each loop, if cross(vector(start, i), vector(start, next)) < 0, let next = i; since cross product 
    is |A||B|Sin(θ), if cross < 0, -180 < θ < 0, so point[i] is on convex hull. Optimized method is sort
    point based on θ to x-axis of leftmost, then scan one by one to find convex hull.*
    
9.  **PolygonPointRelation** Given a list points identify a polygon and a point X, write code to get the 
    relation of X and polygon. The relation should be "INTERIOR", "EXTERIOR", or "BOUNDARY".
    
    *HINT: solve based on cross product, also could solve by Point-Line-Distance and Line-Line-Intersection.*
    
        Solve by calculating cross product of Vector(points[i], points[i+1]) and Vector(points[i], X), 
        if the cross product is:
            negative, X is in the right of Line(point[i+1],[i]),
            positive, X is in the left of Line(point[i+1],[i]),
            0, X is on the Line(point[i+1],[i]), but may not on segment, if on segment, BOUNDARY
        If X is in the right of all edges, X is INTERIOR, otherwise X is in left of one edge, X is EXTERIOR.
        If X is on the segment, X is BOUNDARY.
    
        Solution using Point-Line-Distance and Line-Line-Intersection.
        If X on boundary of polygon, line.distance(X) == 0
        To determine "INTERIOR", "EXTERIOR", pick a point Y very far away, and count the intersection of
        every line in polygon with Line(X, Y), if count is even, X is out of polygon, if is odd, X is in polygon.

10. **TVTower** There is N towns, write code to find where to locate our TV station's broadcasting tower. 
    The location the tower should minimize the broadcast radius that includes all the towns.
    
    *HINT: solve based on ThreePointOnCircle.*
    
        Three points can identity a circle, but for min radius circle cover all the town, there are two cases:
        1. If X and Y are diameter of a circle, the midpoint of X and Y are center, and the radius is min.
        2. If X, Y, Z are all on the circle, center could be found by ThreePointOnCircle, and radius is min. 
        So loop town to find the center to get the min radius.
            1. loop X and Y, get center = midpoint(X, Y)
            2. loop X, Y, Z, get center = new Circle(X, Y, Z).center
        init tower.radius = Float.MAX_VALUE, update the radius as the max distance between center to any town.
        The solution is O(N^4)

11. Surveyor: A plot has been surveyed. Its boundary consists of segments that form a polygon. 
    Each segment runs either North-South or East-West. Create a class Surveyor that contains a method area that 
    takes as input a String direction and a int[] length and returns the enclosed area.
    For example, direction: "NWWSE", steps: {10,3,7,10,10}, the plot is a 10 x 10 square, return area 100.
    
    *HINT: an polygon can be created based on direction and steps, then return the area of polygon.*

12. **Symmetry** A line of symmetry is a line through the cartesian plane such that if you reflect everything
    from one side of the line to the other, you still have the same image. For example, if the x-axis is a line
    of symmetry, it means that for every point (x,y) there is also a point (x,-y).
    Your task is, given a list of points, determine how many such lines exist.
    
    *HINT: get perpendicular line of each two point, and try to find if reflection point of other point in the 
     given points. If exist, it will be re-calculate point.size()/2 time for each reflection point pair.*
    
13. Given four points, write code to check if the four points can form a rectangle.

    *HINT: assume given point is A,B,C,D, if AB is perpendicular with BC, and AD is perpendicular with CD, than 
    ABCD is a rectangle. Two vector is perpendicular can be identified by dot product == 0.*
    
14. **Closest Pair** Given a set of points, find the pair that is closest (with either metric).
    
    *HINT: Of course, this can be solved in O(N^2) time by considering all the pairs, but a line sweep can 
    reduce this to O(N log N).*
    
        Line sweep algorithm is using a conceptual sweep line or sweep surface to solve various problems in 
        Euclidean space.
        First sort the points based on their X-axis, then scan from left to right, for each points, only only
        interested to scan the points in (current.x - minDistance, current.y + minDistance) and (current.y -
        minDistance, current.y + minDistance) rectangle to revise minDistance.
        In implementation, 
            1. sort points based on x-axis, and keep a leftMost pointer
            2. create a SortedSet(TreeSet) which sort candidates based on their y-axis
            3. scan every points
                3.1. shrink leftMost in candidates based on x-axis, make sure x-axis of candidates all in
                     (current.x - minDistance, current.y + minDistance)
                3.2. search candidates based on y-axis using SortedSet.subSet(upper, lower), make sure only
                     select the points whose y-axis in (current.y - minDistance, current.y + minDistance)
                3.3. for each selected candidate, calculate distance and update minDistance and result.
                3.4. add current points in candidates.
        Sort: O(NlgN), shrink x-axis: O(1), search subset O(lgN), so the whole process is O(NlgN).

15. **Line Segment Intersection** Given a set of horizontal and vertical line segments, write code to 
    returning all intersections between each two of them.
    
    *HINT: based on sweep line, scan segment left to right, keep a TreeSet of available segment.*
    
        1. sort the segment based on start point, and horizontal before vertical. 
        2. create a SortedSet<Integer> to put scanned horizontal segment's y axis, and a PriorityQueue 
        to put scanned horizontal segments sorted based on their end points.
        3. scan every segment:
            3.1. poll() lines in queue if their endpoint < current.start.x, and also remove it's y-axis
            from SortedSet.
            3.2. if current is a horizontal segment, put its y-axis in SortedSet and put itself in queue.
            3.3. if current is vertical, get subset y-axis from SortedSet, each y-axis is a intersection
            point with current segment (current.start.x, y-axis).
        Sort: O(NlgN), offer and poll from queue(lgN), add and remove from SortedSet(lgN), subset: O(lgN)
        so the whole process is O(NlgN)
        
16. Given a set of axis-aligned rectangles, what is the area of their union?

    *HINT: Sweep Line.*
    
##Math
1.  Write code to generate all primes begin 1 to N. 

    *HINT: use the method called Sieve of Eratosthenes. Takes the first prime number and removes all of its multiples.*

2.  Greatest common divisor (GCD) and Lowest common multiple (LCM) of two numbers.
    
    *HINT: GCD can be reduced by do % in turn until b == 0. and LCM is (a * b / GCD(a,b))
    
        if (b==0) return a;   
        return GCD(b,a%b);

3.  Quiz Show. http://community.topcoder.com/stat?c=problem_statement&pm=2989&rd=5869
    
        Using 0 to mark wrong, and 1 to mark right, the code for all 8 event in sample space is 000 ~ 111, with 
        same probability of 1/8. My option of wager is [0, my score], so loop on each wager, to find in how many
        cases I can win, if the possibility > curMax, update the wager.
        
4.  Birthday Odds. http://community.topcoder.com/stat?c=problem_statement&pm=1848&rd=4675
5.  NestedRandomness. http://community.topcoder.com/stat?c=problem_statement&pm=3510&rd=6527
6.  GeneticCrossover. http://community.topcoder.com/stat?c=problem_statement&pm=2974&rd=5875
 
    
##String
1.  **ShortestPalindrome**
    A palindrome is a String that is spelled the same forward and backwards. Given a word, you can adjust it to
    palindrome by adding some chars. The add operation can perform at any offset in the word. 
    Your task is to make base into a palindrome by adding as few letters as possible and return the resulting 
    String. When there is more than one palindrome of minimal length that can be made, return the lexicographically 
    earliest.
    
    *HINT: standard DP problem.*
    
        state: memo String[i][j]: the min adjusted palindrome based on str.substring(i, j);
        init:  when only 0 or 1 char(j - i <= 1), return memo[i][j] = str.substring(front, back);
        function: if str.charAt(i) == str.charAt(j-1), memo[i][j] = str.charAt(i) + memo[i+1][j-1] + str.charAt(j-1);
                  else :
                      option1: str.charAt(i) + memo[i+1][j] + str.charAt(i)
                      option2: str.charAt(j-1) + memo[i][j-1] + str.charAt(j-1)
                  select the shorter one, if in the same length, select based on lexicographically order
                  memo[i][j] = selection
             function loop on len and i, j = i + len;
        result: memo[0][str.length()];
        
2.  CyberLine: http://community.topcoder.com/stat?c=problem_statement&pm=2396&rd=4755
3.  Unlinker: http://community.topcoder.com/stat?c=problem_statement&pm=2912&rd=5849
4.  CheatCode: http://community.topcoder.com/stat?c=problem_statement&pm=1779&rd=4575

##Tree
1.  Given a rooted tree T and two nodes u and v, find the furthest node from the root that is an ancestor for both 
    u and v. T is not need to be a BinaryTree.
    
    *HINT: using SegmentTree do RMQ to achieve .*
        
##Dynamic Programming
1.  **ChristmasTree** 
    
2.  Game of Nim
    Two player join the game, table is init with N coins, they can take 1, 3, 4 coins from the beginning, the player 
    make last move is the winner. Write code to find out all values of N that the first player will win.
    
    *HINT: like jump game, define a win[], win[1] = win[3] = win[4] = true, and for other n, if win[n] = !win[n-1]||
    !win[n-3] || !win[n-4].*
    
    There are n piles of coins. When it is a player's turn he chooses one pile and takes at least one coin from it. 
    If someone is unable to move he loses (so the one who removes the last coin is the winner).
    
    *HINT: Let n1, n2, … nk, be the sizes of the piles. It is a losing position for the player whose turn it is if 
    and only if n1xor n2 xor .. xor nk = 0.*

    
    


     
 



    
    
    