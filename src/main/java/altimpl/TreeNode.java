package altimpl;

/**
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
public class TreeNode {

    TreeNode parentNode;
    TreeNode childNodes[];
    private double area;
    double width, tmp_width;                        //tmp_width is used for back-tracking to previous value of width
    double height, tmp_height;
    double aspectLast;                            //Aspect ratio
    double X;                                    //Coordinates of the rectangle of given width and height
    double Y;

    public TreeNode(double area) {
        this.area = area;
    }

    public TreeNode(TreeNode childrens[]) {
        this.childNodes = childrens;
    }

    int getChildNodesCount() {
        return (childNodes != null) ? childNodes.length : 0;
    }


    public double getArea() {
        if (childNodes != null) {
            area = 0;
            for (TreeNode childNode : childNodes) {
                area += childNode.getArea();
            }
            return area;
        } else {
            return area;
        }
    }

    public void setArea(double area) {
        if (childNodes != null) throw new IllegalStateException("Trying to set explicit size on interims node");
        this.area = area;
    }
}
