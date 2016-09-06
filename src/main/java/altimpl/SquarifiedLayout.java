package altimpl;

public final class SquarifiedLayout {

    private final double worldHeight;
    private final double worldWidth;
    private final double worldOffsetX;
    private final double worldOffsetY;


    private Orientation previousOrientation;
    private int previousIndex;                    //Index value of previous operation..initialised to 0
    private int index, end;
    private double currentWidth;
    private double currentHeight;

    public SquarifiedLayout(double worldOffsetX, double worldOffsetY, double worldWidth, double worldHeight) {
        this.worldOffsetX = worldOffsetX;
        this.worldOffsetY = worldOffsetY;
        this.worldHeight = worldHeight;
        this.worldWidth = worldWidth;
    }

    private void preorder(TreeNode node) {        //To traverse the tree in preorder way of traversal
        if (node.getChildNodesCount() > 0) {
            currentWidth = node.width;
            currentHeight = node.height;
            while (end != node.getChildNodesCount()) {
                squarify(node, node.childNodes);            //squarify function finds the squarified treemap of this node
            }
            for (int i = 0; i < node.getChildNodesCount(); i++) {
                preorder(node.childNodes[i]);
            }
        }
        //else Leaf node!
    }

    private double calculateAspectRatio(double height, double width) {    //gets the aspect ratio of a node
        return Math.max(height / width, width / height);
    }

    //checks if nodes are to be drawn vertically or horizontally (if return value is true then, vertically)
    private Orientation calculateOrientation(double width, double height) {
        return (width > height) ? Orientation.LANDSCAPE : Orientation.PORTRAIT;
    }

    private enum Orientation {
        LANDSCAPE, PORTRAIT
    }

    //find the aspect ratio which is more closer to 1 than the other
    private boolean compareAspect(TreeNode[] child, int end, double aspectCurr) {
        return (Math.abs(aspectCurr - 1) > Math.abs(child[end].aspectLast - 1));
    }

    private void squarify(TreeNode v, TreeNode... child) {
        Orientation orientation = calculateOrientation(currentWidth, currentHeight);
        end = index;
        double sum;
        double aspectCurr = Double.MAX_VALUE;               //setting aspectCurr to maximum value possible helpful for back-tracking
        do {
            if (index != (v.getChildNodesCount() - 1)) {                      //as last item has to placed in the remaining area
                double totalArea = 0;
                for (int t = index; t <= end; t++) {
                    totalArea += child[t].getArea();
                }
                for (int i = index; i <= end; i++) {
                    if (orientation == Orientation.PORTRAIT) {
                        child[i].width = totalArea / currentHeight;
                        child[i].height = child[i].getArea() / child[i].width;
                    } else {
                        child[i].height = totalArea / currentWidth;
                        child[i].width = child[i].getArea() / child[i].height;
                    }
                }
                child[end].aspectLast = calculateAspectRatio(child[end].height, child[end].width); //finding aspect ratio of last item
            }
            if (compareAspect(child, end, aspectCurr) && (index != (v.getChildNodesCount() - 1)) && (end
                    != (v.getChildNodesCount() - 1))) {                    //here again last item has no need to be compared
                //Aspect ratio is closer to 1 ! Adding next item.....
                aspectCurr = child[end].aspectLast;
                for (int i = index; i <= end; i++) {
                    child[i].tmp_height = child[i].height;
                    child[i].tmp_width = child[i].width;
                }
                end++; // add next item

            } else {
                //remove that item
                if (index == (v.getChildNodesCount() - 1))                                       //incrementing end value for last item
                {
                    end++;
                }
                if (end == (v.getChildNodesCount() - 1)) {
                    if (compareAspect(child, end, aspectCurr)) {

                        end++;
                    }
                }
                for (int j = index; j < end; j++) {
                    if (index == (v.getChildNodesCount() - 1)) {                                                //for last item
                        child[j].height = currentHeight;
                        child[j].width = currentWidth;
                    } else {
                        if (end != v.getChildNodesCount()) {
                            child[j].height = child[j].tmp_height;
                            child[j].width = child[j].tmp_width;
                        }
                    }
                    child[j].aspectLast = calculateAspectRatio(child[j].height, child[j].width);
                    if (j == 0) {                                                            //find coordinates of first item
                        child[j].X = worldOffsetX;
                        child[j].Y = worldOffsetY;
                    } else if (j == index) {
                        if (previousOrientation == Orientation.PORTRAIT) {
                            child[index].X = child[previousIndex].X + child[previousIndex].width;
                            child[index].Y = child[previousIndex].Y;
                        } else {
                            child[index].Y = child[previousIndex].Y + child[previousIndex].height;
                            child[index].X = child[previousIndex].X;
                        }
                        previousOrientation = orientation;
                        previousIndex = index;
                    } else {
                        sum = 0;
                        if (orientation == Orientation.PORTRAIT) {
                            for (int k = index; k < j; k++) {
                                sum += child[k].height;
                            }
                            child[j].X = child[index].X;
                            child[j].Y = child[index].Y + sum;
                        } else {
                            for (int k = index; k < j; k++) {
                                sum += child[k].width;
                            }
                            child[j].Y = child[index].Y;
                            child[j].X = child[index].X + sum;
                        }
                    }

                }
                if (end != v.getChildNodesCount()) {
                    if (orientation == Orientation.PORTRAIT) {
                        currentWidth = currentWidth - child[index].width;
                    } else {
                        currentHeight = currentHeight - child[index].height;
                    }
                    index = end;
                    return;
                }
                break;
            }
        }
        while (end != v.getChildNodesCount());

    }

    public TreeNode layout(TreeNode root) {
        previousIndex = 0;
        index = 0;
        end = 0;

        root.parentNode = null;
        root.height = worldHeight;
        root.width = worldWidth;
        previousOrientation = calculateOrientation(worldWidth, worldHeight);

        int number = root.getChildNodesCount();

        TreeNode a[] = root.childNodes;
        double totalSum = 0;
        for (int j = 0; j < root.getChildNodesCount(); j++) {
            totalSum += a[j].getArea();
        }
        double ratio = root.getArea() / totalSum;
        for (int i = 0; i < number; i++) {
            a[i].setArea(a[i].getArea() * ratio);
            a[i].parentNode = root;
            a[i].childNodes = null;
            root.childNodes[i] = a[i];
        }

        if (root.childNodes != null) {
            preorder(root);
        }
        return root;
    }

}